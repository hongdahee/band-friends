package com.hdh.band_project;

import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;

    @ModelAttribute
    public void addUserToModel(Model model, Principal principal){
        if(principal!=null) {
            SiteUser user = userService.getUser(principal.getName());
            model.addAttribute("user", user);
        }
    }
}
