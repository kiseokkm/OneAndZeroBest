package com.sparta.oneandzerobest.newsfeed.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedRequestDtoTest {

    private Validator validator;
    private NewsfeedRequestDto dto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        dto = new NewsfeedRequestDto();
    }

    @Test
    @DisplayName("NewsfeedRequestDto validation test for blank content")
    void testContentNotBlankValidation() {
        // Given
        dto.setContent("");

        // When
        Set<ConstraintViolation<NewsfeedRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals("내용이 비어있습니다.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("NewsfeedRequestDto validation success test")
    void testSuccessfulValidation() {
        // Given
        dto.setContent("Valid content");

        // When
        Set<ConstraintViolation<NewsfeedRequestDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }
}
