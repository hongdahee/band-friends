package com.hdh.band_project.post;

import com.hdh.band_project.band.Band;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostForm {
    @NotEmpty(message = "제목을 입력해 주세요.")
    private String subject;

    @NotEmpty(message = "내용을 입력해 주세요.")
    private String content;

    private List<MultipartFile> mediaList;

    private Boolean isClosed;

    private Boolean isPrivate;

    private Band band;

    private List<Position> position;
}
