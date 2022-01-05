package com.example.secondproject.dto;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentRegisterDto {
    private Long id;

    @Size(min = 1, max = 50, message = "내용을 최소 1, 최대 50글자를 입력해주세요")
    private String content;
}
