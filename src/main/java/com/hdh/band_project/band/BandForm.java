package com.hdh.band_project.band;

import com.hdh.band_project.post.Position;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class BandForm {
    @NotEmpty(message = "밴드 이름을 입력해 주세요.")
    private String bandName;

    private MultipartFile profileImg;

    @NotNull(message = "장르를 선택해 주세요.")
    private Genre genre;

    private Boolean isActive;

    @NotNull(message = "포지션을 선택해 주세요.")
    private Position position;

    @NotEmpty(message = "가입질문을 입력해 주세요.")
    private String question;
}