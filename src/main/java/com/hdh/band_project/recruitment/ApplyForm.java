package com.hdh.band_project.recruitment;

import com.hdh.band_project.post.Position;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyForm {
    @NotEmpty(message = "답변을 입력해 주세요.")
    private String answer;

    @NotNull(message = "포지션을 선택해 주세요.")
    private Position position;
}
