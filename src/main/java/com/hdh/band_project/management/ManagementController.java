package com.hdh.band_project.management;

import com.hdh.band_project.band.Band;
import com.hdh.band_project.band.BandCountDto;
import com.hdh.band_project.band.BandMemberService;
import com.hdh.band_project.band.BandService;
import com.hdh.band_project.recruitment.MemberRecruitment;
import com.hdh.band_project.recruitment.RecruitmentService;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management")
public class ManagementController {

    private final RecruitmentService recruitmentService;
    private final BandMemberService bandMemberService;
    private final UserService userService;
    private final BandService bandService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/apply")
    public String applyManagement(Model model, Principal principal){
        SiteUser user = userService.getUser(principal.getName());

        List<Band> bandList = bandMemberService.getBandsWithLeaderRole(user.getId());

        List<BandCountDto> bandWithCount = bandList.stream()
                .map(band -> new BandCountDto(
                        band,
                        recruitmentService.getNewApplyCount(band)
                ))
                .collect(Collectors.toList());

        model.addAttribute("bandList", bandWithCount);
        
        return "apply_management";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/band")
    public String bandManagement(Model model, Principal principal){
        SiteUser user = userService.getUser(principal.getName());
        List<Band> bandList = userService.getUserBandList(user);
        model.addAttribute("bandList", bandList);
        return "band_management";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/apply/list")
    public String getApplyList(@RequestParam("bandId") Long bandId, Model model) {
        Band band = bandService.getBand(bandId);

        List<MemberRecruitment> applyList = recruitmentService.getApplyList(band);

        model.addAttribute("applyList", applyList);

        return "apply_list :: applyInfo";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/apply/response/{applyId}")
    public String response(@RequestParam("response") String response, @PathVariable("applyId") Long applyId,
                          Principal principal) {
       MemberRecruitment apply = recruitmentService.getMemberRecruitment(applyId);

        if ("accept".equals(response)) {
            recruitmentService.acceptApply(apply);
        } else if ("reject".equals(response)) {
            recruitmentService.rejectApply(apply);
        }
        return "redirect:/management/apply";
    }

}
