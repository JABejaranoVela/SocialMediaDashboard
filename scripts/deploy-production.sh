#!/usr/bin/env bash
set -Eeuo pipefail

readonly APP_DIR="/srv/apps/social-media-dashboard"
readonly BACKUP_DIR="/srv/backups/social-media-dashboard/mysql"
readonly PROD_ENV="${APP_DIR}/.env.prod"
readonly DEPLOY_ENV="${APP_DIR}/.env.deploy"
readonly COMPOSE_FILE="${APP_DIR}/docker-compose.prod.yml"
readonly COMPOSE_OVERRIDE="${APP_DIR}/docker-compose.prod.override.example.yml"

AUTH_DIR=""
DEPLOY_ENV_TMP=""
BACKUP_TMP=""
GHCR_TOKEN=""

log() {
  printf '[deploy] %s\n' "$*"
}

die() {
  printf '[deploy] ERROR: %s\n' "$*" >&2
  exit 1
}

cleanup() {
  local exit_code=$?
  set +e
  GHCR_TOKEN=""

  if [[ -n "$BACKUP_TMP" && -f "$BACKUP_TMP" ]]; then
    rm -f -- "$BACKUP_TMP"
  fi
  if [[ -n "$DEPLOY_ENV_TMP" && -f "$DEPLOY_ENV_TMP" ]]; then
    rm -f -- "$DEPLOY_ENV_TMP"
  fi
  if [[ -n "$AUTH_DIR" && -d "$AUTH_DIR" ]]; then
    sudo docker --config "$AUTH_DIR" logout ghcr.io >/dev/null 2>&1 || true
    rm -rf -- "$AUTH_DIR"
  fi

  exit "$exit_code"
}
trap cleanup EXIT

require_command() {
  command -v "$1" >/dev/null 2>&1 || die "Required command not found: $1"
}

retry() {
  local attempts="$1"
  local delay="$2"
  local description="$3"
  shift 3

  local attempt
  for ((attempt = 1; attempt <= attempts; attempt++)); do
    if "$@"; then
      log "Health check passed: ${description}"
      return 0
    fi
    if ((attempt < attempts)); then
      log "Waiting for ${description} (${attempt}/${attempts})"
      sleep "$delay"
    fi
  done

  die "Health check failed: ${description}"
}

if (($# != 3)); then
  die "Usage: $0 <deploy-sha> <ghcr-owner> <ghcr-username>"
fi

readonly DEPLOY_SHA="$1"
readonly GHCR_OWNER="$2"
readonly GHCR_USERNAME="$3"

[[ "$DEPLOY_SHA" =~ ^[0-9a-f]{40}$ ]] || die "Deploy SHA must be a full 40-character lowercase commit SHA"
[[ "$GHCR_OWNER" =~ ^[a-z0-9][a-z0-9-]*$ ]] || die "Invalid lowercase GHCR owner"
[[ -n "$GHCR_USERNAME" ]] || die "GHCR username is required"

for command_name in git curl date mktemp grep sudo mv chmod rm sleep; do
  require_command "$command_name"
done

[[ -d "$APP_DIR" ]] || die "Application directory does not exist: $APP_DIR"
[[ -f "$PROD_ENV" ]] || die "Production environment file does not exist: $PROD_ENV"
[[ -f "$COMPOSE_FILE" ]] || die "Compose file does not exist: $COMPOSE_FILE"
[[ -f "$COMPOSE_OVERRIDE" ]] || die "Compose override does not exist: $COMPOSE_OVERRIDE"
[[ -d "$BACKUP_DIR" ]] || die "Backup directory does not exist: $BACKUP_DIR"
[[ -w "$BACKUP_DIR" ]] || die "Backup directory is not writable by the deploy user: $BACKUP_DIR"

sudo -n docker version >/dev/null 2>&1 || die "Passwordless sudo Docker access is unavailable"
sudo -n docker compose version >/dev/null 2>&1 || die "Docker Compose plugin is unavailable"

cd "$APP_DIR"

git diff --quiet || die "Tracked working-tree changes found on VPS"
git diff --cached --quiet || die "Staged changes found on VPS"
[[ -z "$(git ls-files --others --exclude-standard)" ]] || die "Untracked, non-ignored files found on VPS"

export GIT_TERMINAL_PROMPT=0
log "Updating repository to ${DEPLOY_SHA}"
git fetch --prune origin main
git checkout main
git merge --ff-only origin/main
[[ "$(git rev-parse HEAD)" == "$DEPLOY_SHA" ]] || die "VPS revision does not match requested deploy SHA"

DEPLOY_ENV_TMP="$(mktemp "${APP_DIR}/.env.deploy.tmp.XXXXXX")"
chmod 600 "$DEPLOY_ENV_TMP"
{
  printf 'BACKEND_IMAGE=ghcr.io/%s/social-media-dashboard-backend:%s\n' "$GHCR_OWNER" "$DEPLOY_SHA"
  printf 'FRONTEND_IMAGE=ghcr.io/%s/social-media-dashboard-frontend:%s\n' "$GHCR_OWNER" "$DEPLOY_SHA"
} > "$DEPLOY_ENV_TMP"
mv -f -- "$DEPLOY_ENV_TMP" "$DEPLOY_ENV"
DEPLOY_ENV_TMP=""
chmod 600 "$DEPLOY_ENV"
log "Updated persistent deployment manifest: ${DEPLOY_ENV}"

if [[ -d /dev/shm && -w /dev/shm ]]; then
  AUTH_DIR="$(mktemp -d /dev/shm/socialmedia-docker-auth.XXXXXX)"
else
  AUTH_DIR="$(mktemp -d /tmp/socialmedia-docker-auth.XXXXXX)"
fi
chmod 700 "$AUTH_DIR"

docker_auth() {
  sudo docker --config "$AUTH_DIR" "$@"
}

compose() {
  docker_auth compose \
    --env-file "$PROD_ENV" \
    --env-file "$DEPLOY_ENV" \
    -f "$COMPOSE_FILE" \
    -f "$COMPOSE_OVERRIDE" \
    "$@"
}

IFS= read -r GHCR_TOKEN || die "Unable to read GHCR token from stdin"
[[ -n "$GHCR_TOKEN" ]] || die "GHCR token is empty"
printf '%s' "$GHCR_TOKEN" | docker_auth login ghcr.io --username "$GHCR_USERNAME" --password-stdin >/dev/null
GHCR_TOKEN=""

compose config --quiet

readonly SHORT_SHA="${DEPLOY_SHA:0:12}"
TIMESTAMP="$(date -u +%Y%m%d_%H%M%S)"
readonly TIMESTAMP
readonly BACKUP_FILE="${BACKUP_DIR}/socialmedia_predeploy_${TIMESTAMP}_${SHORT_SHA}.sql"
BACKUP_TMP="${BACKUP_DIR}/.socialmedia_predeploy_${TIMESTAMP}_${SHORT_SHA}.sql.tmp"

rm -f -- "$BACKUP_TMP"
log "Creating MySQL backup"
# Variables in this command are intentionally expanded inside the db container.
# shellcheck disable=SC2016
if ! compose exec -T db sh -c \
  'export MYSQL_PWD="$MYSQL_PASSWORD"; exec mysqldump -u"$MYSQL_USER" --single-transaction --quick --no-tablespaces "$MYSQL_DATABASE"' \
  > "$BACKUP_TMP"; then
  die "mysqldump failed"
fi

[[ -f "$BACKUP_TMP" ]] || die "Backup temporary file was not created"
[[ -s "$BACKUP_TMP" ]] || die "Backup temporary file is empty"
chmod 640 "$BACKUP_TMP"
mv -f -- "$BACKUP_TMP" "$BACKUP_FILE"
BACKUP_TMP=""
log "MySQL backup created: ${BACKUP_FILE}"

log "Pulling immutable images"
compose pull

log "Starting production services"
compose up -d --no-build

check_backend() {
  curl -fsS --max-time 10 http://127.0.0.1:9091/actuator/health | grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"'
}

check_frontend() {
  curl -fsS --max-time 10 http://127.0.0.1:8082/healthz >/dev/null
}

check_public_frontend() {
  local status
  status="$(curl -sS -I --max-time 15 -o /dev/null -w '%{http_code}' https://socialmedia.jabejarano.tech)"
  [[ "$status" =~ ^2[0-9]{2}$ ]]
}

check_public_api() {
  curl -fsS --max-time 15 https://socialmedia.jabejarano.tech/api/dashboard/respondent/count | grep -q '[^[:space:]]'
}

retry 30 5 "backend actuator" check_backend
retry 30 5 "frontend container" check_frontend
retry 20 5 "public frontend" check_public_frontend
retry 20 5 "public API" check_public_api

log "Service status"
compose ps
docker_auth ps --format 'table {{.Names}}\t{{.Ports}}'

log "Pruning dangling Docker images older than seven days"
if ! docker_auth image prune -f --filter "until=168h"; then
  log "The Docker version rejected the age filter; pruning dangling images without an age filter"
  docker_auth image prune -f
fi

log "Deployment completed successfully for ${DEPLOY_SHA}"
