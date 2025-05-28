package com.hdh.band_project.band;

import com.hdh.band_project.chat.ChatRoom;
import com.hdh.band_project.chat.ChatRoomService;
import com.hdh.band_project.post.Position;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/band")
public class BandController {

    private final BandService bandService;
    private final BandMemberService bandMemberService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/register")
    public String register(Principal principal, BandForm bandForm, Model model){
        SiteUser user = userService.getUser(principal.getName());

        if(user.getRegionName()==null || user.getRegionName().isBlank() || user.getLatitude()==null || user.getLongitude()==null){
            return "redirect:/user/location/verify";
        }

        List<Genre> genreList = Arrays.asList(Genre.values());
        model.addAttribute("genreList", genreList);

        List<Position> positionList = Arrays.asList(Position.values());
        model.addAttribute("positionList", positionList);
        model.addAttribute("regionName", user.getRegionName());
        return "band_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public String register(@Valid BandForm bandForm, BindingResult bindingResult,
                           Principal principal){
        SiteUser user = userService.getUser(principal.getName());

        Long bandId = bandService.create(bandForm.getBandName(), bandForm.getGenre(),
                bandForm.getQuestion(), user.getRegionName(), user.getLatitude(),
                user.getLongitude());
        Band band = bandService.getBand(bandId);

        bandMemberService.create(bandForm.getPosition(), band, user, MemberRole.LEADER);
        ChatRoom chatRoom = chatRoomService.create(band);
        bandService.registerChatRoom(band, chatRoom);

        return "redirect:/band/home/" + bandId;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/home/{id}")
    public String home(Model model, @PathVariable("id") Long id) {
        Band band = bandService.getBand(id);
        List<BandMember> sortedMemberList = bandService.sortMemberList(band);

        model.addAttribute("band", band);
        model.addAttribute("memberList", sortedMemberList);
        return "band/band_home";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/setting/{id}")
    public String setting(Model model, @PathVariable("id") Long id,
                          Principal principal, BandForm bandForm, BandProfileForm bandProfileForm) {
        SiteUser user = userService.getUser(principal.getName());
        bandMemberService.checkBandMember(user, id);

        List<Genre> genreList = Arrays.asList(Genre.values());
        model.addAttribute("genreList", genreList);

        Band band = bandService.getBand(id);
        BandMember bandMember = bandMemberService.getBandMember(user, id);
        boolean isLeader = bandMemberService.isLeader(bandMember);
        List<BandMember> sortedMemberList = bandService.sortMemberList(band);

        bandForm.setBandName(band.getBandName());
        bandForm.setGenre(band.getGenre());
        bandForm.setQuestion(band.getQuestion());

        model.addAttribute("band", band);
        model.addAttribute("isLeader", isLeader);
        model.addAttribute("currentPosition", bandMember.getPosition());
        model.addAttribute("currentGenre", band.getGenre());
        List<Position> positionList = Arrays.asList(Position.values());
        model.addAttribute("positionList", positionList);
        model.addAttribute("memberList", sortedMemberList);
        return "band/setting";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id, @Valid BandForm bandForm,
                         BindingResult bindingResult, Principal principal){
        SiteUser user = userService.getUser(principal.getName());
        bandService.modify(id, bandForm, user);
        return "redirect:/band/setting/"+id;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload/profile/{id}")
    public String uploadProfile(@PathVariable("id") Long bandId, @Valid BandProfileForm bandProfileForm,
                                BindingResult bindingResult, Principal principal){
        SiteUser user = userService.getUser(principal.getName());
        bandService.updateProfile(bandId, bandProfileForm.getProfileImg(), user);
        return "redirect:/band/setting/"+bandId;
    }

//    @PreAuthorize("@leaderPermissionEvaluator.isLeader(#id, authentication)")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal) {
        SiteUser user = userService.getUser(principal.getName());
        bandService.delete(id, user);
        return "redirect:/recruitment/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/quit/{id}")
    public String quit(@PathVariable("id") Long id, Principal principal) {
        SiteUser user = userService.getUser(principal.getName());
        bandService.quit(id, user);
        return "redirect:/recruitment/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/position/{id}")
    public String modifyPosition(@PathVariable("id") Long id, Principal principal,
                                 @RequestParam("position") Position position) {
        SiteUser user = userService.getUser(principal.getName());
        bandMemberService.modifyPosition(id, user, position);
        return "redirect:/band/setting/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/staff/{bandId}/{memberId}")
    public String modifyStaffStatus(@PathVariable("bandId") Long bandId,
                                    @PathVariable("memberId") Long memberId, Principal principal) {
        SiteUser user = userService.getUser(principal.getName());
        bandMemberService.modifyStaffStatus(bandId, memberId, user);
        return "redirect:/band/setting/" + bandId;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/leader/{bandId}/{memberId}")
    public String changeLeader(@PathVariable("bandId") Long bandId,
                               @PathVariable("memberId") Long memberId, Principal principal) {
        SiteUser user = userService.getUser(principal.getName());
        bandMemberService.changeLeader(bandId, memberId, user);
        return "redirect:/band/setting/" + bandId;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/kick/{bandId}/{memberId}")
    public String kickMember(@PathVariable("bandId") Long bandId,
                               @PathVariable("memberId") Long memberId, Principal principal) {
        SiteUser user = userService.getUser(principal.getName());
        bandMemberService.kickMember(bandId, memberId, user);
        return "redirect:/band/setting/" + bandId;
    }
}
