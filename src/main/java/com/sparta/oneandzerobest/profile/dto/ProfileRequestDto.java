package com.sparta.oneandzerobest.profile.dto;

import lombok.Getter;

@Getter
public class ProfileRequestDto {
    private String name;
    private String introduction;
    private String password;
    private String newPassword;

    public void setName(String name) {
        this.name = name;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
