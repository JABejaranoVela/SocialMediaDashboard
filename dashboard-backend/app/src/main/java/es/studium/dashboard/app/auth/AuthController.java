package es.studium.dashboard.app.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsersService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        Users user = userService.findByUsername(username);

        if (user != null && userService.checkPassword(user, password)) {
            String token = jwtUtil.generateToken(username, user.getRole());
            return Map.of("token", token, "role", user.getRole());
        }
        throw new RuntimeException("Usuario o contraseña incorrectos");
    }
}
