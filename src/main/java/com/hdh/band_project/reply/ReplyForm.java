package com.hdh.band_project.reply;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyForm {
    @NotEmpty(message = "내용을 입력해 주세요.")
    private String content;
}
