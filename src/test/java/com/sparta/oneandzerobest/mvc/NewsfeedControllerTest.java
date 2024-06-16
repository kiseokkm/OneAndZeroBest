package com.sparta.oneandzerobest.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oneandzerobest.newsfeed.controller.NewsfeedController;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.service.NewsfeedService;
import com.sparta.oneandzerobest.s3.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsfeedController.class)
public class NewsfeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsfeedService newsfeedService;

    @MockBean
    private ImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("뉴스피드 생성 테스트")
    @WithMockUser
    void postNewsfeedTest() throws Exception {
        // Given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("샘플");
        NewsfeedResponseDto responseDto = new NewsfeedResponseDto(1L, "샘플");

        given(newsfeedService.postContent(any(String.class), any(NewsfeedRequestDto.class))).willReturn(ResponseEntity.ok(responseDto));

        // When - Then
        mockMvc.perform(post("/newsfeed")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("뉴스피드 조회 테스트")
    @WithMockUser
    void getAllNewsfeedTest() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        given(newsfeedService.getAllContents(any(Integer.class), any(Integer.class), any(Boolean.class), any(Boolean.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(ResponseEntity.ok(Page.empty()));

        // When - Then
        mockMvc.perform(get("/newsfeed")
                        .param("page", "0")
                        .param("size", "10")
                        .param("isASC", "true")
                        .param("like", "false")
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("뉴스피드 수정 테스트")
    @WithMockUser
    void updateNewsfeedTest() throws Exception {
        // Given
        Long newsfeedId = 1L;
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("뉴스피드 수정");
        NewsfeedResponseDto responseDto = new NewsfeedResponseDto(newsfeedId, "뉴스피드 수정");

        given(newsfeedService.putContent(any(String.class), any(Long.class), any(NewsfeedRequestDto.class))).willReturn(ResponseEntity.ok(responseDto));

        // When - Then
        mockMvc.perform(put("/newsfeed/{id}", newsfeedId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("뉴스피드 삭제 테스트")
    @WithMockUser
    void deleteNewsfeedTest() throws Exception {
        // Given
        Long newsfeedId = 1L;

        // When - Then
        mockMvc.perform(delete("/newsfeed/{id}", newsfeedId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("뉴스피드 이미지 업로드 테스트")
    @WithMockUser
    void uploadImageToNewsfeedTest() throws Exception {
        // Given
        Long newsfeedId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some-image".getBytes());

        given(imageService.uploadImageToNewsfeed(any(String.class), any(Long.class), any(MultipartFile.class))).willReturn(ResponseEntity.ok("이미지 업로드 성공"));

        // When - Then
        mockMvc.perform(multipart("/newsfeed/media")
                        .file(file)
                        .param("id", String.valueOf(newsfeedId))
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("뉴스피드 이미지 수정 테스트")
    @WithMockUser
    void updateImageToNewsfeedTest() throws Exception {
        // Given
        Long newsfeedId = 1L;
        Long fileId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "newfile.jpg", "image/jpeg", "new-image".getBytes());

        given(imageService.updateImageToNewsfeed(any(String.class), any(MultipartFile.class), any(Long.class), any(Long.class))).willReturn(ResponseEntity.ok("이미지 수정 성공"));

        // When - Then
        mockMvc.perform(multipart("/newsfeed/media")
                        .file(file)
                        .param("id", String.valueOf(newsfeedId))
                        .param("fileId", String.valueOf(fileId))
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}
