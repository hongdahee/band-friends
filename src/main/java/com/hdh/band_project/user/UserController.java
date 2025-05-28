package com.hdh.band_project.user;

import com.hdh.band_project.oauth.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @Value("${kakao.map.api.key}")
    private String kakaoMapApiKey;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm){
        return "signup_form";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult,
                         HttpServletRequest request){
        if(bindingResult.hasErrors()){
            return "signup_form";
        }
        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try{
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(),
                    userCreateForm.getPassword1(), userCreateForm.getNickname());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            String validateResult = userService.validate(userCreateForm.getUsername(), userCreateForm.getEmail());

            if(validateResult.equals("username")) {
                bindingResult.rejectValue("username", "signupFailed", "이미 등록된 아이디입니다.");
            }
            else if(validateResult.equals("email")) {
                bindingResult.rejectValue("email", "signupFailed", "이미 등록된 이메일입니다.");
            }
            else if(validateResult.equals("all")){
                bindingResult.rejectValue("username", "signupFailed", "이미 등록된 아이디입니다.");
                bindingResult.rejectValue("email", "signupFailed", "이미 등록된 이메일입니다.");
            }
            else{
                bindingResult.reject("signupFailed", "회원 가입에 실패했습니다. 다시 시도해주세요.");
            }

            return "signup_form";
        }catch (Exception e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
        }
        userService.autoLogin(userCreateForm.getEmail(), request);
        return "redirect:/user/location/verify";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("kakaoApiKey", kakaoService.getKakaoApiKey());
        model.addAttribute("redirectUri", kakaoService.getKakaoRedriectUri());
        return "login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/{uniqueId}")
    public String userInfo(Model model, @PathVariable("uniqueId") String uniqueId){
        SiteUser user = userService.getUserByUniqueId(uniqueId);
        model.addAttribute("user", user);
        return "/user/user_info";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/edit")
    public String editHome(Model model, Principal principal, UserEditForm userEditForm){
        SiteUser user = userService.getUser(principal.getName());
        userEditForm.setNickname(user.getNickname());
        model.addAttribute("user", user);
        return "/user/info_edit";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/info/edit/nickname")
    public String editNickname(Principal principal, Model model, @Valid UserEditForm userEditForm){
        SiteUser user = userService.getUser(principal.getName());
        userService.editNickname(userEditForm.getNickname(), user);
        model.addAttribute("user", user);
        return "redirect:/user/info/"+user.getUniqueId();
    }

   @PreAuthorize("isAuthenticated()")
   @PostMapping("/info/edit/profile")
        public String editProfile(@RequestParam("profile") MultipartFile profileImg, Principal principal,
                                  Model model){
            SiteUser user = userService.getUser(principal.getName());
            userService.editUserInfo(profileImg, user);
            model.addAttribute("user", user);
            return "redirect:/user/info/" + user.getUniqueId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/account/edit")
    public String accountEdit(){
        return "/user/account_edit";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete")
    public String accountDelete(Principal principal, HttpServletRequest request, HttpServletResponse response){
        SiteUser user = userService.getUser(principal.getName());
        userService.delete(user, request, response);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/location/save")
    public String saveLocation(Principal principal, @RequestParam("regionName") String regionName,
                               @RequestParam("lat") Double lat, @RequestParam("lon") Double lon){
        SiteUser user = userService.getUser(principal.getName());
        userService.saveLocation(user, regionName, lat, lon);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("location/verify")
    public String verify(Model model){
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
        return "geolocation/verify";
    }
}
