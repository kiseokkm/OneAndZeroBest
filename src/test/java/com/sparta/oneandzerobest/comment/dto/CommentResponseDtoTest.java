package com.sparta.oneandzerobest.comment.dto;

import com.sparta.oneandzerobest.comment.entity.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentResponseDtoTest {

    private Comment comment;
    private CommentResponseDto dto;

    @BeforeEach
    void setUp() {
        comment = new Comment(1L, 2L, "Test comment");
        dto = new CommentResponseDto(comment);
    }

    @Test
    @DisplayName("CommentResponseDto 생성자 테스트")
    void testConstructor() {
        // Given

        // When

        // Then
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getNewsfeedId(), dto.getNewsfeedId());
        assertEquals(comment.getUserId(), dto.getUserId());
        assertEquals(comment.getContent(), dto.getContent());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getModifiedAt());
    }
}
