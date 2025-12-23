package com.hdh.band_project.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해 주세요.")
    @NotEmpty(message = "아이디는 필수항목입니다.")
    private String username;

    @Size(min = 6, max = 20,  message = "비밀번호는 6자 이상 20자 이하로 입력해 주세요.")
    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*+=()_-])(?=.*[0-9]).+$",
            message = "비밀번호는 영문, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;

    @NotEmpty(message = "닉네임은 필수항목입니다.")
    private String nickname;

    private String profileImg;
}
