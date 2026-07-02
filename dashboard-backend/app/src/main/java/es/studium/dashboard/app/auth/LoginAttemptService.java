package es.studium.dashboard.app.auth;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptService {

    static final int MAX_FAILURES = 5;
    static final Duration WINDOW = Duration.ofMinutes(15);
    static final Duration BLOCK_DURATION = Duration.ofMinutes(15);
    private static final int MAX_ENTRIES = 10_000;
    private static final int CLEANUP_INTERVAL = 100;

    private final ConcurrentHashMap<String, AttemptState> attempts = new ConcurrentHashMap<>();
    private final AtomicInteger operations = new AtomicInteger();

    public boolean isBlocked(String key) {
        Instant now = Instant.now();
        cleanupOpportunistically(now);
        AttemptState state = attempts.get(key);
        if (state == null || state.blockedUntil() == null) {
            return false;
        }
        if (!state.blockedUntil().isAfter(now)) {
            attempts.remove(key, state);
            return false;
        }
        return true;
    }

    public long retryAfterSeconds(String key) {
        AttemptState state = attempts.get(key);
        if (state == null || state.blockedUntil() == null) {
            return 0;
        }
        return Math.max(1, Duration.between(Instant.now(), state.blockedUntil()).toSeconds());
    }

    public void recordFailure(String key) {
        Instant now = Instant.now();
        ensureCapacity(now, key);
        attempts.compute(key, (ignored, current) -> {
            if (current == null || !current.windowStarted().plus(WINDOW).isAfter(now)) {
                return new AttemptState(1, now, null, now);
            }
            if (current.blockedUntil() != null && current.blockedUntil().isAfter(now)) {
                return current;
            }
            int failures = current.failures() + 1;
            Instant blockedUntil = failures >= MAX_FAILURES ? now.plus(BLOCK_DURATION) : null;
            return new AttemptState(failures, current.windowStarted(), blockedUntil, now);
        });
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }

    private void cleanupOpportunistically(Instant now) {
        if (operations.incrementAndGet() % CLEANUP_INTERVAL != 0 && attempts.size() < MAX_ENTRIES) {
            return;
        }
        attempts.entrySet().removeIf(entry -> isExpired(entry.getValue(), now));
    }

    private void ensureCapacity(Instant now, String key) {
        cleanupOpportunistically(now);
        if (attempts.size() < MAX_ENTRIES || attempts.containsKey(key)) {
            return;
        }
        attempts.entrySet().stream()
                .min(Comparator.comparing(entry -> entry.getValue().lastAttempt()))
                .map(Map.Entry::getKey)
                .ifPresent(attempts::remove);
    }

    private boolean isExpired(AttemptState state, Instant now) {
        if (state.blockedUntil() != null) {
            return !state.blockedUntil().isAfter(now);
        }
        return !state.windowStarted().plus(WINDOW).isAfter(now);
    }

    private record AttemptState(int failures, Instant windowStarted, Instant blockedUntil, Instant lastAttempt) {
    }
}
