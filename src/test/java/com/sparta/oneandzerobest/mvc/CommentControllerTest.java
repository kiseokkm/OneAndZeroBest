package com.sparta.oneandzerobest.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oneandzerobest.comment.controller.CommentController;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    @WithMockUser(username="user", roles={"USER"})
    void testCreateComment() throws Exception {
        // Given
        Long newsfeedId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("해윙");
        CommentResponseDto responseDto = new CommentResponseDto(1L, newsfeedId, 2L, "해윙", "2024-01-01 10:00:00", "2024-01-01 10:00:00");

        given(commentService.addComment(any(Long.class), any(CommentRequestDto.class), any(String.class))).willReturn(responseDto);

        // When
        mockMvc.perform(post("/newsfeed/{newsfeedAccountId}/comments", newsfeedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        // Then 
    }

    @Test
    @DisplayName("모든 댓글 조회 테스트")
    @WithMockUser(username="user", roles={"USER"})
    void testGetAllComments() throws Exception {
        // Given
        Long newsfeedId = 1L;
        CommentResponseDto responseDto = new CommentResponseDto(1L, newsfeedId, 2L, "댓글조회", "2024-01-01 10:00:00", "2024-01-01 10:00:00");
        given(commentService.getAllComments(newsfeedId, "token")).willReturn(List.of(responseDto));

        // When - Then
        mockMvc.perform(get("/newsfeed/{newsfeedAccountId}/comments", newsfeedId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(responseDto))));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    @WithMockUser(username="user", roles={"USER"})
    void testUpdateComment() throws Exception {
        // Given
        Long newsfeedId = 1L;
        Long commentId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("Updated comment!");
        CommentResponseDto updatedResponse = new CommentResponseDto(commentId, newsfeedId, 2L, "댓글수정함", "2024-01-01 12:00:00", "2024-01-01 12:00:00");

        given(commentService.updateComment(newsfeedId, commentId, requestDto, "token")).willReturn(updatedResponse);

        // When - Then
        mockMvc.perform(put("/newsfeed/{newsfeedAccountId}/comments/{commentId}", newsfeedId, commentId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedResponse)));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    @WithMockUser(username="user", roles={"USER"})
    void testDeleteComment() throws Exception {
        // Given
        Long newsfeedId = 1L;
        Long commentId = 1L;

        // When - Then
        mockMvc.perform(delete("/newsfeed/{newsfeedAccountId}/comments/{commentId}", newsfeedId, commentId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}
