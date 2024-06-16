package com.sparta.oneandzerobest.newsfeed.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsfeedRequestDto {
    @NotBlank(message = "내용이 비어있습니다.")
    private String content;
}
