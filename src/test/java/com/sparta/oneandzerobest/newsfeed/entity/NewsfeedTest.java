package com.sparta.oneandzerobest.newsfeed.entity;

import com.sparta.oneandzerobest.newsfeed_like.entity.NewsfeedLike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedTest {

    private Newsfeed newsfeed;
    private NewsfeedLike newsfeedLike;

    @BeforeEach
    void setUp() {
        // Given
        newsfeed = new Newsfeed(1L, "Test content");
        newsfeedLike = new NewsfeedLike();
        newsfeed.setNewsfeedLike(newsfeedLike);
    }

    @Test
    @DisplayName("뉴스피드 좋아요 추가 테스트")
    void testAddNewsfeedLike() {
        // When
        newsfeed.setNewsfeedLike(new NewsfeedLike());

        // Then
        assertEquals(2, newsfeed.getNewsfeedLikeList().size());
        assertEquals(2, newsfeed.getLikeCount());
    }

    @Test
    @DisplayName("뉴스피드 좋아요 제거 테스트")
    void testRemoveNewsfeedLike() {
        // Given

        // When
        newsfeed.removeNewsfeedLike(newsfeedLike);

        // Then
        assertTrue(newsfeed.getNewsfeedLikeList().isEmpty());
        assertEquals(0, newsfeed.getLikeCount());
    }
}
