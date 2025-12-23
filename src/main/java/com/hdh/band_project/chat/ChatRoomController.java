package com.hdh.band_project.chat;

import com.hdh.band_project.band.Band;
import com.hdh.band_project.band.BandMemberService;
import com.hdh.band_project.band.BandService;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final UserService userService;
    private final ChatService chatService;
    private final BandService bandService;
    private final BandMemberService bandMemberService;

    @Value("${kakao.map.api.key}")
    private String kakaoMapApiKey;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chat")
    public String chatRoom(Model model, @RequestParam("id") Long id, Principal principal){
        SiteUser user = userService.getUser(principal.getName());
        bandMemberService.checkBandMember(user, id);
        List<ChatMessage> messageList = chatService.getMessageList(id);
        Band band = bandService.getBandByChatRoomId(id);

        String[] formattedRegionName = band.getRegionName().trim().split("\\s+");

        model.addAttribute("id", id);
        model.addAttribute("user", user);
        model.addAttribute("messageList", messageList);
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
        model.addAttribute("regionName", formattedRegionName[formattedRegionName.length - 1]);

        return "band_chat";
    }
}
