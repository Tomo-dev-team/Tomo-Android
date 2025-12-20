package com.example.tomo.Moim.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class addMoimRequestDto {

    @NotBlank(message = "모임명이 누락되었습니다.")
    private String title;

    @NotBlank(message = "모임 설명이 누락되었습니다.")
    private String description;

    @NotBlank(message = "모임 참가자 이메일이 누락되었습니다.")
    private List<String> emails; // 사용자 이름 , 이메일로
}
