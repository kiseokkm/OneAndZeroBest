package com.sparta.oneandzerobest.auth.service;

import com.sparta.oneandzerobest.auth.entity.*;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final String username = "testUser1";
    private final String email = "test1@Email.com";
    private final String password = "testPassword1!";
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(username, "encodedPassword", "testName", email, UserStatus.UNVERIFIED);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testSignup() {
        // Given
        SignupRequest signupRequest = new SignupRequest(username, password, email, false, "");
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        userService.signup(signupRequest);

        // Then
        verify(userRepository).save(any(User.class));
        assertDoesNotThrow(() -> userService.signup(signupRequest));
    }

    @Test
    @DisplayName("이메일 인증 테스트")
    void testEmailVerification() {
        // Given
        String verificationCode = "012345";
        redisTemplate.opsForValue().set(username, verificationCode);

        // When
        boolean result = userService.verifyEmail(username, verificationCode);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("로그인 테스트")
    void testLogin() {
        // Given
        LoginRequest loginRequest = new LoginRequest(username, password);
        user.updateStatus(UserStatus.ACTIVE);

        // When
        LoginResponse loginResponse = userService.login(loginRequest);

        // Then
        assertNotNull(loginResponse.getAccessToken());
        assertNotNull(loginResponse.getRefreshToken());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void testLogout() {
        // Given
        user.updateStatus(UserStatus.ACTIVE);
        user.setRefreshToken("someRefreshToken");

        // When
        userService.logout(username, "someAccessToken", "someRefreshToken");

        // Then
        assertNull(user.getRefreshToken());
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void testWithdraw() {
        // Given
        user.updateStatus(UserStatus.ACTIVE);

        // When
        userService.withdraw(username, password, "someAccessToken", "someRefreshToken");

        // Then
        assertEquals(UserStatus.WITHDRAWN, user.getStatusCode());
        assertNull(user.getRefreshToken());
    }

    @Test
    @DisplayName("OAuth 로그인 테스트")
    void testOAuthLogin() {
        // When
        LoginResponse loginResponse = userService.loginWithOAuth(email);

        // Then
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getAccessToken());
    }

    @Test
    @DisplayName("소셜 로그인 통합 테스트 (카카오, 구글, 깃헙)")
    void testSocialLogins() {
        // Given
        String userInfoJson = "{\"id\": 12345, \"properties\": {\"nickname\": \"testUser\"}}";
        String[] services = {"카카오", "구글", "깃헙"};
        String[] emails = {username + "@kakao.com", username + "@google.com", username + "@github.com"};

        for (int i = 0; i < services.length; i++) {
            // When
            User result = userService.saveOrUpdateKakaoUser(userInfoJson);

            // Then
            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals(emails[i], result.getEmail());
        }
    }
}
