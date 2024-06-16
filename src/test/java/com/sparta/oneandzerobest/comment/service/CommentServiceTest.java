package com.sparta.oneandzerobest.comment.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.comment.repository.CommentRepository;
import com.sparta.oneandzerobest.exception.CommentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Autowired
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, userRepository, jwtUtil);
    }

    @Test
    @DisplayName("댓글 등록 테스트")
    void testAddComment() {
        // Given
        Long newsfeedId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto(newsfeedId, "댓글등록 ");
        String token = "Bearer testToken";
        User user = new User("testUsername", "encodedPassword", "testName123", "test1@example.com", UserStatus.ACTIVE);
        user.setId(1L); // ID 설정
        Comment comment = new Comment(newsfeedId, user.getId(), commentRequestDto.getContent());

        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        CommentResponseDto commentResponseDto = commentService.addComment(newsfeedId, commentRequestDto, token);

        // Then
        assertEquals(comment.getContent(), commentResponseDto.getContent());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("뉴스피드의 모든 댓글 조회 테스트")
    void testGetAllComments() {
        // Given
        Long newsfeedId = 1L;
        String token = "Bearer testToken";
        User user = new User("testUsername", "encodedPassword", "testName1", "test123@example.com", UserStatus.ACTIVE);
        List<Comment> comments = new ArrayList<>(Arrays.asList(
                new Comment(newsfeedId, user.getId(), "test1 댓글"),
                new Comment(newsfeedId, user.getId(), "test2 댓글"),
                new Comment(newsfeedId, user.getId(), "test3 댓글")
        ));

        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findByNewsfeedId(newsfeedId)).thenReturn(comments);

        // When
        List<CommentResponseDto> result = commentService.getAllComments(newsfeedId, token);

        // Then
        assertEquals(comments.size(), result.size());
        assertEquals(comments.get(0).getContent(), result.get(0).getContent());
        assertEquals(comments.get(1).getContent(), result.get(1).getContent());
        assertEquals(comments.get(2).getContent(), result.get(2).getContent());

        verify(commentRepository, times(1)).findByNewsfeedId(newsfeedId);
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void testUpdateComment() {
        // Given
        Long newsfeedId = 1L;
        Long commentId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto(newsfeedId, "댓글수정");
        String token = "Bearer testToken";
        User user = new User("testUsername", "encodedPassword", "testName", "test1@example.com", UserStatus.ACTIVE);
        user.setId(1L);
        Comment comment = new Comment(newsfeedId, user.getId(), "original comment");
        comment.setId(commentId);

        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId())).thenReturn(Optional.of(comment));

        // When
        CommentResponseDto commentResponseDto = commentService.updateComment(newsfeedId, commentId, commentRequestDto, token);

        // Then
        assertEquals(commentRequestDto.getContent(), commentResponseDto.getContent());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void testDeleteComment() {
        // Given
        Long newsfeedId = 1L;
        Long commentId = 1L;
        String token = "Bearer testToken";
        User user = new User("testUsername", "encodedPassword", "testName123", "test1@example.com", UserStatus.ACTIVE);
        user.setId(1L);
        Comment comment = new Comment(newsfeedId, user.getId(), "댓글삭제");
        comment.setId(commentId);

        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId())).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(newsfeedId, commentId, token);

        // Then
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 시 권한 없을 때 예외 테스트")
    void testDeleteCommentUnauthorized() {
        // Given
        Long newsfeedId = 1L;
        Long commentId = 1L;
        String token = "Bearer testToken";
        User user = new User("testUsername", "encodedPassword", "testName1234", "test1@example.com", UserStatus.ACTIVE);
        user.setId(1L);

        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(newsfeedId, commentId, token));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId());
    }
}