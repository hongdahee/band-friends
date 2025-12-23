package com.hdh.band_project.band;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BandProfileForm {
    @NotNull
    private MultipartFile profileImg;
}
