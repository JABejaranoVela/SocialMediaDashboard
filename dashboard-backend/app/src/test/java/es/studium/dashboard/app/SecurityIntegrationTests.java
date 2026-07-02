package es.studium.dashboard.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.studium.dashboard.app.auth.JwtUtil;
import es.studium.dashboard.app.auth.Users;
import es.studium.dashboard.app.auth.UsersRepository;
import es.studium.dashboard.app.model.Respondent;
import es.studium.dashboard.app.repository.RespondentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTests {

    private static final String TEST_PASSWORD = "test-password";
    private static final String UPDATE_BODY = """
            {
              "age": 42,
              "gender": "Test",
              "demographics": {
                "relationshipStatus": "Single",
                "occupationStatus": "Employed"
              },
              "socialMediaUsage": {
                "usesSocialMedia": "Yes",
                "dailyAverageTime": "1-2 hours",
                "aimlessUsageFrequency": 1,
                "distractionFrequency": 2,
                "restlessnessFrequency": 3
              },
              "mentalHealthMetrics": {
                "easilyDistractedScale": 1,
                "worryIntensityScale": 2,
                "difficultyConcentrating": 3,
                "socialComparisonFrequency": 4,
                "comparisonFeeling": 5,
                "validationSeekingFrequency": 1,
                "depressedFrequency": 2,
                "interestFluctuationScale": 3,
                "sleepIssueScale": 4
              },
              "organizationName": "",
              "platforms": []
            }
            """;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private RespondentRepository respondentRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;

    private Users userA;
    private Users userB;
    private Respondent respondentA;
    private Respondent respondentB;

    @BeforeEach
    void setUp() {
        respondentRepository.deleteAll();
        usersRepository.deleteAll();
        userA = saveUser("user-a", "user");
        userB = saveUser("user-b", "user");
        respondentA = saveRespondent(userA, 30);
        respondentB = saveRespondent(userB, 35);
    }

    @Test
    void respondentResponseAndUsersSerializationNeverExposePassword() throws Exception {
        mockMvc.perform(get("/api/respondents/{id}", respondentA.getRespondentId())
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value(userA.getUsername()))
                .andExpect(jsonPath("$.user.role").value(userA.getRole()))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("password"))));

        assertThat(objectMapper.writeValueAsString(userA)).doesNotContain("password", userA.getPassword());
    }

    @Test
    void respondentCollectionsAreScopedToTheAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/respondents/by-user").header("Authorization", bearer(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].respondentId").value(respondentA.getRespondentId()));

        mockMvc.perform(get("/api/respondents").header("Authorization", bearer(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].respondentId").value(respondentA.getRespondentId()));
    }

    @Test
    void ownerCanReadUpdateAndDeleteOwnRespondent() throws Exception {
        mockMvc.perform(get("/api/respondents/{id}", respondentA.getRespondentId())
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/respondents/{id}", respondentA.getRespondentId())
                        .header("Authorization", bearer(userA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(42));

        mockMvc.perform(delete("/api/respondents/{id}", respondentA.getRespondentId())
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isNoContent());

        assertThat(respondentRepository.findById(respondentA.getRespondentId())).isEmpty();
    }

    @Test
    void authenticatedCreateKeepsTheExistingContractAndAssignsThePrincipal() throws Exception {
        mockMvc.perform(post("/api/respondents")
                        .header("Authorization", bearer(userA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value(userA.getUsername()))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void userCannotReadUpdateOrDeleteAnotherUsersRespondent() throws Exception {
        mockMvc.perform(get("/api/respondents/{id}", respondentB.getRespondentId())
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/respondents/{id}", respondentB.getRespondentId())
                        .header("Authorization", bearer(userA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/respondents/{id}", respondentB.getRespondentId())
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isNotFound());

        assertThat(respondentRepository.findById(respondentB.getRespondentId())).isPresent();
    }

    @Test
    void respondentEndpointsRequireAuthenticationAndDebugEndpointIsGone() throws Exception {
        mockMvc.perform(get("/api/respondents")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/respondents/{id}", respondentA.getRespondentId()))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/respondents/test").header("Authorization", bearer(userA)))
                .andExpect(status().isNotFound());
    }

    @Test
    void aggregateDashboardRemainsPublic() throws Exception {
        mockMvc.perform(get("/api/dashboard/respondent/count"))
                .andExpect(status().isOk());
    }

    @Test
    void invalidLoginReturnsUnauthorizedThenRateLimitReturnsTooManyRequests() throws Exception {
        for (int attempt = 0; attempt < 5; attempt++) {
            mockMvc.perform(loginRequest("missing-user", "wrong", "198.51.100.10"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
        }

        mockMvc.perform(loginRequest("missing-user", "wrong", "198.51.100.10"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }

    @Test
    void successfulLoginClearsPreviousFailures() throws Exception {
        mockMvc.perform(loginRequest(userA.getUsername(), "wrong", "198.51.100.20"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(loginRequest(userA.getUsername(), TEST_PASSWORD, "198.51.100.20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        for (int attempt = 0; attempt < 5; attempt++) {
            mockMvc.perform(loginRequest(userA.getUsername(), "wrong", "198.51.100.20"))
                    .andExpect(status().isUnauthorized());
        }
        mockMvc.perform(loginRequest(userA.getUsername(), "wrong", "198.51.100.20"))
                .andExpect(status().isTooManyRequests());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder loginRequest(
            String username, String password, String remoteAddress) {
        return post("/api/auth/login")
                .with(request -> {
                    request.setRemoteAddr(remoteAddress);
                    return request;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
    }

    private Users saveUser(String username, String role) {
        Users user = new Users();
        user.setUsername(username);
        user.setRole(role);
        user.setPassword(new BCryptPasswordEncoder().encode(TEST_PASSWORD));
        return usersRepository.save(user);
    }

    private Respondent saveRespondent(Users owner, int age) {
        Respondent respondent = new Respondent();
        respondent.setTimestamp(LocalDateTime.now());
        respondent.setAge(age);
        respondent.setGender("Test");
        respondent.setUser(owner);
        return respondentRepository.save(respondent);
    }

    private String bearer(Users user) {
        return "Bearer " + jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}
