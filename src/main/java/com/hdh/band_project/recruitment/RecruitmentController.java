package com.hdh.band_project.recruitment;

import com.hdh.band_project.band.Band;
import com.hdh.band_project.band.BandMemberService;
import com.hdh.band_project.band.BandService;
import com.hdh.band_project.geolocation.GeoService;
import com.hdh.band_project.post.Position;
import com.hdh.band_project.post.Post;
import com.hdh.band_project.post.PostService;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recruitment")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;
    private final PostService postService;
    private final UserService userService;
    private final BandMemberService bandMemberService;
    private final GeoService geoService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/apply/{id}")
    public String apply(Model model, @PathVariable("id") Long id, ApplyForm applyForm,
                        Principal principal) {
        SiteUser user = userService.getUser(principal.getName());
        Post post = postService.getPost(id);
        Band band = post.getBand();
        Long bandId = band.getId();

        geoService.checkApplyBandByDistance(user.getLatitude(), user.getLongitude(), band.getLatitude(), band.getLongitude());

        Boolean isBandMember = bandMemberService.isBandMember(user, bandId);
        Boolean hasApplied = recruitmentService.hasApplied(band, user);
        Boolean isClosed = postService.isClosed(post);

        if(isBandMember || hasApplied || isClosed){
            return "redirect:/recruitment/detail/{id}";
        }

        List<Position> positionList = Arrays.asList(Position.values());

        model.addAttribute("positionList", positionList);
        model.addAttribute("postId", post.getId());
        model.addAttribute("question", band.getQuestion());
        
        return "apply_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/apply/{id}")
    public String apply(@Valid ApplyForm applyForm, BindingResult bindingResult,
                        @PathVariable("id") Long id, Principal principal) {
        Band band = postService.getPost(id).getBand();
        SiteUser user = userService.getUser(principal.getName());

        recruitmentService.apply(applyForm, band, user);

        return "redirect:/recruitment/list";
    }
}
