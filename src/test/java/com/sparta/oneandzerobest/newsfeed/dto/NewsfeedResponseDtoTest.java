package com.sparta.oneandzerobest.newsfeed.dto;

import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewsfeedResponseDtoTest {

    private Newsfeed newsfeed;
    private NewsfeedResponseDto dto;

    @BeforeEach
    void setUp() {
        newsfeed = new Newsfeed(1L, "Test content");
        dto = new NewsfeedResponseDto(newsfeed);
    }

    @Test
    @DisplayName("NewsfeedResponseDto constructor test")
    void testConstructor() {
        // Given

        // When

        // Then
        assertEquals(newsfeed.getUserid(), dto.getUserid());
        assertEquals(newsfeed.getContent(), dto.getContent());
    }
}
