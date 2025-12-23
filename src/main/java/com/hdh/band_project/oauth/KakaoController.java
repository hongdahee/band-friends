package com.hdh.band_project.oauth;

import com.hdh.band_project.user.UserCreateForm;
import com.hdh.band_project.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class KakaoController {

    private final KakaoService kakaoService;
    private final UserService userService;

    @GetMapping("/kakao/callback")
    public String redirect(@RequestParam("code") String code, UserCreateForm userCreateForm,
                           Model model, HttpServletRequest httpServletRequest){
        String accessToken = kakaoService.getAccessToken(code);
        String email = kakaoService.getUserEmail(accessToken);
        Boolean isUserExists = userService.checkUserExistsByEmail(email);
        if(email.length() > 1){
            if(!isUserExists) {
                userCreateForm.setEmail(email);
                model.addAttribute("oauth", true);
                model.addAttribute("oauthEmail", email);
                return "signup_form";
            }
            else{
                userService.autoLogin(email, httpServletRequest);
                return "redirect:/";
            }
        }
        else{
            return "redirect:/user/login";
        }
    }
}
