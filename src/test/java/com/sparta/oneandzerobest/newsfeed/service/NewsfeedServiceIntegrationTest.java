package com.sparta.oneandzerobest.newsfeed.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class NewsfeedServiceIntegrationTest {

    @Autowired
    private NewsfeedService newsfeedService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NewsfeedRepository newsfeedRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("게시글 작성 테스트")
    void testPostContent() {
        // Given
        String token = "Bearer testToken";
        String username = "testUsername";
        Long userId = 1L;
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto("게시글작성");
        User user = new User(username, "testPassword1!", "testName", "test1@example.com", UserStatus.ACTIVE);
        user.setId(userId);

        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(newsfeedRepository.save(any(Newsfeed.class))).thenReturn(new Newsfeed(userId, newsfeedRequestDto.getContent()));

        // When
        ResponseEntity<NewsfeedResponseDto> response = newsfeedService.postContent(token, newsfeedRequestDto);

        // Then
        assertNotNull(response);
        assertEquals(user.getId(), response.getBody().getUserid());
        assertEquals(newsfeedRequestDto.getContent(), response.getBody().getContent());
        verify(userRepository, times(1)).findByUsername(username);
        verify(newsfeedRepository, times(1)).save(any(Newsfeed.class));
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void testGetAllContents() {
        // Given
        int page = 0;
        int size = 10;
        boolean isASC = true;
        boolean like = false;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        List<Newsfeed> newsfeeds = Arrays.asList(
                new Newsfeed(1L, "게시글1"),
                new Newsfeed(2L, "게시글2")
        );

        Page<Newsfeed> newsfeedPage = new PageImpl<>(newsfeeds);
        when(newsfeedRepository.findAll(any(Pageable.class))).thenReturn(newsfeedPage);

        // When
        ResponseEntity<Page<NewsfeedResponseDto>> response = newsfeedService.getAllContents(
                page, size, isASC, like, startTime, endTime);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(newsfeeds.size());
        verify(newsfeedRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("게시물 수정 테스트")
    void testPutContent() {
        // Given
        String token = "Bearer testToken";
        String username = "testUsername";
        Long contentId = 1L;
        String updateContent = "updated content";
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto(updateContent);
        User user = new User(username, "testPassword1!", "testName", "test1@example.com", UserStatus.ACTIVE);
        user.setId(1L);
        Newsfeed newsfeed = new Newsfeed(user.getId(), "게시물수정");

        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(newsfeedRepository.findById(contentId)).thenReturn(Optional.of(newsfeed));

        // When
        ResponseEntity<NewsfeedResponseDto> response = newsfeedService.putContent(token, contentId, newsfeedRequestDto);

        // Then
        assertNotNull(response);
        assertEquals(updateContent, response.getBody().getContent());
        verify(userRepository, times(1)).findByUsername(username);
        verify(newsfeedRepository, times(1)).findById(contentId);
    }

    @Test
    @DisplayName("게시물 삭제 테스트")
    void testDeleteContent() {
        // Given
        String token = "Bearer testToken";
        Long contentId = 1L;
        String username = "testUsername";
        User user = new User(username, "testPassword1!", "testName", "test1@example.com", UserStatus.ACTIVE);
        user.setId(1L);
        Newsfeed newsfeed = new Newsfeed(user.getId(), "게시물삭제");

        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(newsfeedRepository.findById(contentId)).thenReturn(Optional.of(newsfeed));

        // When
        ResponseEntity<Long> response = newsfeedService.deleteContent(token, contentId);

        // Then
        assertNotNull(response);
        assertEquals(contentId, response.getBody());
        verify(newsfeedRepository, times(1)).findById(contentId);
        verify(userRepository, times(1)).findByUsername(username);
    }
}
