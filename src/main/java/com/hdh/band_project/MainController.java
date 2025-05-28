package com.hdh.band_project;

import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class MainController {
    @GetMapping("/")
    public String root(){
        return "redirect:/community/list";
    }
}
