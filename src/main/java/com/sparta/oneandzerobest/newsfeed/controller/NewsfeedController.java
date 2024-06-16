package com.sparta.oneandzerobest.newsfeed.controller;

import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.service.NewsfeedService;
import com.sparta.oneandzerobest.s3.service.ImageService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/newsfeed")
public class NewsfeedController {

    private final NewsfeedService newsfeedService;
    private final ImageService s3UploadService;

    /**
     * 뉴스피드 생성
     *
     * @param token
     * @param contentRequestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<NewsfeedResponseDto> postNewsfeed(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody NewsfeedRequestDto contentRequestDto) {

        return newsfeedService.postContent(token, contentRequestDto);
    }

    /**
     * 뉴스피드 조회
     *
     * @param page
     * @param size
     * @param isASC     오름차순 , 내림차순
     * @param like      false면 생성일 기준으로 , true면 좋아요 순
     * @param startTime 시작날짜 (required = false)
     * @param endTime   최종 날짜 (required = false)
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<NewsfeedResponseDto>> getAllNewsfeed(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("isASC") boolean isASC,
            @RequestParam("like") boolean like,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {

        return newsfeedService.getAllContents(page, size, isASC, like, startTime, endTime);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsfeedResponseDto> putNewsfeed(
            @RequestHeader("Authorization") String token, @PathVariable Long id,
            @Valid @RequestBody NewsfeedRequestDto contentRequestDto) {

        return newsfeedService.putContent(token, id, contentRequestDto);
    }

    /**
     * 뉴스피드 삭제
     *
     * @param token
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteNewsfeed(@RequestHeader("Authorization") String token,
                                               @PathVariable Long id) {
        return newsfeedService.deleteContent(token, id);
    }

    /**
     * 뉴스피드에 사진 업로드
     *
     * @param file
     * @param id
     * @return
     */
    @PostMapping("/media")
    public ResponseEntity<String> uploadImageToNewsfeed(
            @RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file,
            @RequestParam Long id) {

        return s3UploadService.uploadImageToNewsfeed(token,id, file);
    }

    @PutMapping("/media")
    public ResponseEntity<String> updateImageToNewsfeed(
            @RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file,
            @RequestParam Long id, @RequestParam Long fileid) {

        return s3UploadService.updateImageToNewsfeed(token,file, id, fileid);
    }
}
