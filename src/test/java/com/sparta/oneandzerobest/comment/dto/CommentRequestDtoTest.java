package com.sparta.oneandzerobest.comment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentRequestDtoTest {

    private CommentRequestDto dto;

    @BeforeEach
    void setUp() {
        dto = new CommentRequestDto();
        dto.setNewsfeedId(1L);
        dto.setContent("Test content");
    }

    @Test
    @DisplayName("CommentRequestDto 필드 할당 테스트")
    void testFieldAssignment() {
        // Given

        // When

        // Then
        assertEquals(1L, dto.getNewsfeedId());
        assertEquals("Test content", dto.getContent());
    }
}
