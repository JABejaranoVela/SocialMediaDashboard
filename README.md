# SocialMediaDashboard

**Live demo:** [jabejaranosocialmediadashboard.netlify.app](https://jabejaranosocialmediadashboard.netlify.app/)

## Description

`SocialMediaDashboard` is an interactive web dashboard that helps explore how **social media usage** relates to different **mental health indicators** using survey-based data.

The application is built with a decoupled architecture:

- **Backend:** Java + Spring Boot (REST API) with **JWT authentication**
- **Frontend:** Vue (SPA) powered by **Vite**, using **CoreUI** for the UI
- **Database:** MySQL (relational schema for respondents, demographics, usage metrics, mental health metrics, platforms, organizations, and users)

The dataset used to design and test the application comes from the public Kaggle dataset **“Social Media and Mental Health”**.

---

## Key features

- **Authentication & authorization (JWT)**
  - Secure login and protected routes
- **Interactive analytics dashboard**
  - KPI cards (e.g., respondents count, usage %, average distraction)
  - Charts and summaries for demographics and platform usage
- **Advanced filtering / exploration**
  - Analyze trends by age group, gender, occupation, platforms, etc.
- **Data management (authenticated users)**
  - Create new survey records (“Ingresar registro”)
  - Edit/delete records from a management table (“Modificar registro”)
  - Records are user-scoped (each user sees/manages their own entries)

---

## Tech stack

### Backend
- Java + Spring Boot (REST)
- Spring Data JPA (Hibernate)
- MySQL connector
- JWT (io.jsonwebtoken)

### Frontend
- Vue (SPA)
- Vite (dev server + build)
- Vue Router
- CoreUI (UI components/layout)

---

## Requirements

- **Java JDK** (use the version defined by the backend `pom.xml`)
- **Maven**
- **Node.js + npm**
- **MySQL Server**

---

## Installation

### 1) Clone the repository

```bash
git clone https://github.com/JABejaranoVela/SocialMediaDashboard.git
cd SocialMediaDashboard
```

### 2) Database setup (MySQL)

1. Create a database (example name: `social_media_dashboard`).
2. Run the SQL scripts located in the `db/` folder to create tables and (optionally) seed data.

> Note: If you prefer, you can also import the dataset first into a staging table and then normalize it into relational tables (as described in the project documentation).

### 3) Backend setup (Spring Boot)

Go to the backend folder and configure the database connection and JWT secret in `application.properties` (or via environment variables):

```bash
cd dashboard-backend/app
```

Run the backend:

```bash
mvn spring-boot:run
```

The backend is expected to run on:

- `http://localhost:9090`

### 4) Frontend setup (Vue + Vite)

In a new terminal:

```bash
cd dashboard-frontend
npm install
npm run dev
```

The frontend will start the Vite dev server (usually on `http://localhost:5173`).

During development, the frontend is configured to proxy API calls:

- Requests to `/api` → `http://localhost:9090`

---

## Usage

1. Start **MySQL**
2. Start the **backend** (Spring Boot)
3. Start the **frontend** (Vite)

Then open the frontend in your browser and use the sidebar navigation:

- **Inicio**: public dashboard overview
- **Iniciar sesión**: login form
- After login:
  - **Ingresar registro**
  - **Modificar registro**
  - **Cerrar sesión**

### Main routes (frontend)
- `/dashboard` → main analytics dashboard
- `/login` → authentication
- `/register` → create a new record
- `/edit` and `/edit-form` → record management / edit flow

---

## Configuration

### Backend (recommended)
- Do not hardcode secrets in `application.properties` for production.
- Use environment variables for:
  - DB URL / username / password
  - JWT secret

### Frontend
- The dev proxy is configured in `vite.config.js` so `/api` calls hit the backend locally.

---

## Notes & limitations

- The dataset is survey-based and intended for educational/analytical purposes.
- This project is designed for local development and demonstration. If deploying publicly, review:
  - secret management (JWT, DB credentials)
  - CORS configuration
  - HTTPS and production build strategy

---

## Production-like Docker Compose stack

The production Compose configuration runs three services on a private Docker network:

- `frontend`: the Vue production build served by Nginx. Its internal Nginx proxies `/api/` to the backend so the complete stack also works locally through the frontend URL.
- `backend`: the Spring Boot application running with the `prod` profile on port 9090 inside Docker.
- `db`: MySQL 8 with persistent storage. MySQL has no host port and does not expose port 3306 publicly.

The base Compose file publishes no host ports. The example override binds the frontend and backend exclusively to loopback (`127.0.0.1:8082` and `127.0.0.1:9091`), ready for a future host Nginx reverse proxy.

### Configuration

Create a local production environment file and replace every placeholder with strong, independent values:

```powershell
Copy-Item .env.prod.example .env.prod
```

`.env.prod` is ignored by Git and must never be committed. `JWT_SECRET` must be long enough for the configured HMAC JWT algorithm; use at least 32 random bytes. Keep the MySQL and Spring datasource credentials aligned.

### Build and run locally

Use both Compose files for local access through loopback:

```powershell
docker compose --env-file .env.prod -f docker-compose.prod.yml -f docker-compose.prod.override.example.yml config --quiet
docker compose --env-file .env.prod -f docker-compose.prod.yml -f docker-compose.prod.override.example.yml build
docker compose --env-file .env.prod -f docker-compose.prod.yml -f docker-compose.prod.override.example.yml up -d
docker compose --env-file .env.prod -f docker-compose.prod.yml -f docker-compose.prod.override.example.yml ps
```

Check the services:

```powershell
curl.exe -fsS http://127.0.0.1:9091/actuator/health
curl.exe -fsS -I http://127.0.0.1:8082
curl.exe -fsS http://127.0.0.1:8082/healthz
curl.exe -fsS http://127.0.0.1:8082/api/dashboard/respondent/count
```

Stop the stack without deleting its database volume:

```powershell
docker compose --env-file .env.prod -f docker-compose.prod.yml -f docker-compose.prod.override.example.yml down
```

The file `db/dashboard.sql` is processed by the official MySQL image only when the database volume is empty. Restarting containers does not re-import it. Deleting the volume destroys local database data and causes the dump to be imported again on the next start.

This SQL dump is an initial deployment mechanism, not a migration system. Flyway or Liquibase should be introduced before production schema evolution. The current dump also contains a demo `admin` account with a BCrypt password hash; review and replace that credential before any real deployment.

The Netlify site remains available as the original demo and can coexist as a fallback. The production deployment is available at `https://socialmedia.jabejarano.tech`.

---

## Production CI/CD

Pushes to `main` run `.github/workflows/deploy-production.yml`. The workflow can also be started manually with `workflow_dispatch` and serializes production runs without cancelling a deployment already in progress.

The pipeline contains four stages:

1. Backend validation with Java 21, Maven dependency caching, tests and packaging.
2. Frontend validation with Node.js 22, `npm ci` and the Vite production build.
3. Build and push of the backend and frontend images to GHCR using the full commit SHA, a 12-character SHA and `main` tags.
4. SSH deployment of the full-SHA images to the VPS after a mandatory MySQL backup.

The deployed image references are always the immutable full-SHA tags:

- `ghcr.io/jabejaranovela/social-media-dashboard-backend:<sha>`
- `ghcr.io/jabejaranovela/social-media-dashboard-frontend:<sha>`

GitHub Actions publishes and temporarily pulls the images with the repository `GITHUB_TOKEN`; no separate GHCR token secret is required.

### Required GitHub secrets

- `VPS_HOST`: VPS hostname or IP address.
- `VPS_USER`: SSH deployment user (`deploy` in production).
- `VPS_SSH_KEY`: private SSH key used only by the workflow.
- `VPS_KNOWN_HOSTS`: pinned SSH host key entry. The workflow enforces strict host-key checking.

The production environment values remain in `/srv/apps/social-media-dashboard/.env.prod` and are never generated by CI. Each successful deployment atomically updates `.env.deploy` with the exact backend and frontend image tags. `.env.deploy` contains no secrets, remains on the VPS for manual Compose operations and must not be deleted after deployment.

### Predeploy MySQL backup

Before pulling or starting new images, `scripts/deploy-production.sh` writes a dump to:

```text
/srv/backups/social-media-dashboard/mysql/socialmedia_predeploy_<timestamp>_<short-sha>.sql
```

The dump is first written to a hidden `.sql.tmp` file. Deployment stops if `mysqldump` fails or the file is empty; only a valid dump is moved to its final name. Backups use `--single-transaction`, `--quick` and the mandatory `--no-tablespaces` option, which avoids the MySQL 8 `PROCESS` privilege requirement.

### Deployment checks and diagnostics

The remote script verifies a clean checkout, performs a fast-forward-only update and confirms that the VPS commit equals the workflow SHA. It then validates Compose, creates the backup, pulls images and starts services without local builds.

After startup it checks:

- `http://127.0.0.1:9091/actuator/health`
- `http://127.0.0.1:8082/healthz`
- `https://socialmedia.jabejarano.tech`
- `https://socialmedia.jabejarano.tech/api/dashboard/respondent/count`

On failure, inspect the failed workflow step, the printed `docker compose ps` output when available, and on the VPS use the same `.env.prod` plus `.env.deploy` files to inspect Compose logs. Fix the cause and rerun the failed workflow with `workflow_dispatch`, or push a new commit to `main`.

This first CI/CD version has no automatic rollback. A failed health check marks the workflow as failed but preserves the database, backups, previous images and generated `.env.deploy`. Never delete these production assets while diagnosing a deployment:

- `/srv/data/social-media-dashboard/mysql`
- `/srv/backups/social-media-dashboard/mysql`
- `/srv/apps/social-media-dashboard/.env.prod`
- `/srv/apps/social-media-dashboard/.env.deploy`

Container image cleanup is limited to dangling images. Backup retention, automatic rollback, schema migrations and blocking npm vulnerability audits remain separate follow-up work; the current known npm findings do not block this workflow.

---

## Resources

- Kaggle dataset (name): `Social Media and Mental Health`
- REST API + JWT authentication
- Vue + Vite SPA architecture

---

## License

This project is licensed under the MIT License. You are free to use, modify and distribute it under the terms of that license.
