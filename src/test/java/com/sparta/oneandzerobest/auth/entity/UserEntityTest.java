package com.sparta.oneandzerobest.auth.entity;

import com.sparta.oneandzerobest.profile.dto.ProfileRequestDto;
import com.sparta.oneandzerobest.s3.entity.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityTest {

    User user;
    String username;
    String password;
    String name;
    String email;
    UserStatus statusCode;

    @BeforeEach
    void setUp() {
        username = "testUsername";
        password = "testPassword";
        name = "testName";
        email = "testEmail";
        statusCode = UserStatus.UNVERIFIED;
        user = new User(username, password, name, email, statusCode);
    }

    @Test
    @DisplayName("User 생성자 테스트")
    void testConstructor() {
        // Given

        // When

        // Then
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("onCreate() 메소드 테스트")
    void testOnCreate() {
        // Given - 초기 상태 확인
        assertNull(user.getUpdatedAt());

        // When
        user.onCreate();

        // Then
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("onUpdate() 메소드 테스트")
    void testOnUpdate() {
        // Given - 초기 상태 확인
        user.onCreate(); // 초기 createdAt 및 updatedAt 설정
        LocalDateTime initialUpdatedAt = user.getUpdatedAt();

        // When
        user.onUpdate();

        // Then
        assertTrue(initialUpdatedAt.isBefore(user.getUpdatedAt()));
    }

    @Test
    @DisplayName("getAuthorities() 메소드 테스트")
    void testGetAuthorities() {
        // Given

        // When
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Then
        assertTrue(authorities.isEmpty());
    }

    @Test
    @DisplayName("setProfileImage() 메소드 테스트")
    void testSetProfileImage() {
        // Given
        Image image = Mockito.mock(Image.class);

        // When
        user.setProfileImage(image);

        // Then
        assertEquals(image, user.getImage());
    }

    @Test
    @DisplayName("update() 메소드 테스트")
    void testUpdate() {
        // Given
        ProfileRequestDto requestDto = new ProfileRequestDto();
        requestDto.setName("updatedName");
        requestDto.setIntroduction("updatedIntroduction");

        // When
        user.update(requestDto);

        // Then
        assertEquals("updatedName", user.getName());
        assertEquals("updatedIntroduction", user.getIntroduction());
    }
}
