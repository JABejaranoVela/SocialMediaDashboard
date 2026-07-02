package es.studium.dashboard.app.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String INVALID_CREDENTIALS = "Credenciales incorrectas";

    private final UsersService userService;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UsersService userService, JwtUtil jwtUtil, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Map<String, String> loginData,
            HttpServletRequest request) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String attemptKey = clientIp(request) + ":" + normalizeUsername(username);

        if (loginAttemptService.isBlocked(attemptKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, String.valueOf(loginAttemptService.retryAfterSeconds(attemptKey)))
                    .body(Map.of("message", "Demasiados intentos de acceso"));
        }

        Users user = username == null || username.isBlank() ? null : userService.findByUsername(username);
        if (user == null || password == null || !userService.checkPassword(user, password)) {
            loginAttemptService.recordFailure(attemptKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", INVALID_CREDENTIALS));
        }

        loginAttemptService.recordSuccess(attemptKey);
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole()));
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int lastSeparator = forwardedFor.lastIndexOf(',');
            String proxyAddress = forwardedFor.substring(lastSeparator + 1).trim();
            if (!proxyAddress.isEmpty() && proxyAddress.length() <= 64) {
                return proxyAddress;
            }
        }
        return request.getRemoteAddr();
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
