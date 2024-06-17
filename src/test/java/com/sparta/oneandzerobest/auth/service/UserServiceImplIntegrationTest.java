package com.sparta.oneandzerobest.auth.service;

import com.sparta.oneandzerobest.auth.email.service.EmailService;
import com.sparta.oneandzerobest.auth.entity.SignupRequest;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.exception.IdPatternException;
import com.sparta.oneandzerobest.exception.InfoNotCorrectedException;
import com.sparta.oneandzerobest.exception.PasswordPatternException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userService = new UserServiceImpl(userRepository, passwordEncoder, emailService, redisTemplate, jwtUtil);
    }

    @Test
    @DisplayName("아이디 유효성 예외 테스트")
    void testIdPatternException() {
        // Given
        SignupRequest signupRequest = new SignupRequest("testUser", "testPassword1!", "test1@Email.com", false, "");

        // When
        Exception exception = assertThrows(IdPatternException.class, () -> userService.signup(signupRequest));

        // Then
        assertEquals("아이디는 최소 10자 이상, 20자 이하이며 알파벳 대소문자와 숫자로 구성되어야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 유효성 예외 테스트")
    void testPasswordPatternException() {
        // Given
        SignupRequest signupRequest = new SignupRequest("Testusername", "password", "test1@email.com", false, "");

        // When
        Exception exception = assertThrows(PasswordPatternException.class, () -> userService.signup(signupRequest));

        // Then
        assertNotNull(exception);
    }

    @Test
    @DisplayName("중복 ID 회원 예외 테스트")
    void testDuplicateUsername() {
        // Given
        String username = "ExistUsername";
        SignupRequest signupRequest = new SignupRequest(username, "Password11!", "test1@email.com", false, "");
        userRepository.save(new User(username, "password", "user1", "user1@email.com", UserStatus.ACTIVE));

        // When
        Exception exception = assertThrows(InfoNotCorrectedException.class, () -> userService.signup(signupRequest));

        // Then
        assertEquals("중복된 사용자 ID가 존재합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("중복된 이메일 예외 테스트")
    void testDuplicateEmail() {
        // Given
        String email = "test@email.com";
        SignupRequest signupRequest = new SignupRequest("TestUsername", "Password11!", email, false, "");
        userRepository.save(new User("username", "password", "user1", email, UserStatus.ACTIVE));

        // When
        Exception exception = assertThrows(InfoNotCorrectedException.class, () -> userService.signup(signupRequest));

        // Then
        assertEquals("중복된 이메일이 존재합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("이메일 업데이트 테스트")
    void testUpdateEmail() {
        // Given
        String authId = "testUser";
        SignupRequest signupRequest = new SignupRequest(authId, "password", "update@email.com", false, "");
        User user = new User(authId, passwordEncoder.encode("password"), "testUser", "not@email.com", UserStatus.UNVERIFIED);
        userRepository.save(user);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(authId, "someValue");

        // When
        userService.updateEmail(signupRequest);

        // Then
        User updatedUser = userRepository.findByUsername(authId).orElseThrow();
        assertEquals(signupRequest.getEmail(), updatedUser.getEmail());
    }
}
