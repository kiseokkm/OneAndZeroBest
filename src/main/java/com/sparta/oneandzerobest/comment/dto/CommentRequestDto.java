package com.sparta.oneandzerobest.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * CommentRequestDto는 뉴스피드에 댓글을 추가하는 데 필요한 데이터를 담는 DTO
 * 댓글 내용과 해당 뉴스피드의 ID를 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @NotNull(message = "뉴스피드 ID는 필수입니다.")
    private Long newsfeedId;

    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    @Size(max = 255, message = "댓글은 최대 255자까지 입력 가능합니다.")
    private String content;
}
