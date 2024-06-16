package com.sparta.oneandzerobest.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthDtoTest {
    @Test
    @DisplayName("RefreshTokenRequestDto 테스트")
    void testRefreshTokenDTO() {
        // Given
        String testToken = "test_refresh_token";
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
        refreshTokenRequestDto.setRefreshToken(testToken);

        // When
        String refreshToken = refreshTokenRequestDto.getRefreshToken();

        // Then
        assertEquals(testToken, refreshToken);
    }

    @Test
    @DisplayName("SignupRequestDto 테스트")
    void testSignupRequestDto() {
        // Given
        String testUsername = "username1";
        String testPassword = "password1";
        String testName = "name1";
        String testEmail = "email1";
        SignupRequestDto signupRequestDto = new SignupRequestDto(testUsername, testPassword, testName, testEmail);

        // When- 실행하고

        // Then
        assertEquals(testUsername, signupRequestDto.getUsername());
        assertEquals(testPassword, signupRequestDto.getPassword());
        assertEquals(testName, signupRequestDto.getName());
        assertEquals(testEmail, signupRequestDto.getEmail());
    }

    @Test
    @DisplayName("TokenResponseDto 테스트")
    void testTokenResponseDto() {
        // Given
        String testAccessToken = "test_access_token";
        String testRefreshToken = "test_refresh_token";
        TokenResponseDto tokenResponseDto = new TokenResponseDto(testAccessToken, testRefreshToken);

        // When

        // Then
        assertEquals(testAccessToken, tokenResponseDto.getAccessToken());
        assertEquals(testRefreshToken, tokenResponseDto.getRefreshToken());
    }
}
