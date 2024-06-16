package com.sparta.oneandzerobest.comment.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("Comment 생성자 및 필드 할당 테스트")
    void testCommentConstructorAndFields() {
        // Given
        Long expectedNewsfeedId = 1L;
        Long expectedUserId = 2L;
        String expectedContent = "This is a test comment";

        // When
        Comment comment = new Comment(expectedNewsfeedId, expectedUserId, expectedContent);

        // Then
        assertEquals(expectedNewsfeedId, comment.getNewsfeedId());
        assertEquals(expectedUserId, comment.getUserId());
        assertEquals(expectedContent, comment.getContent());
    }

    @Test
    @DisplayName("setModifiedAt() 메서드 테스트")
    void testSetModifiedAt() {
        // Given
        Comment comment = new Comment();
        LocalDateTime now = LocalDateTime.now();

        // When
        comment.setModifiedAt(now);

        // Then
        assertEquals(now, comment.getModifiedAt());
    }
}
