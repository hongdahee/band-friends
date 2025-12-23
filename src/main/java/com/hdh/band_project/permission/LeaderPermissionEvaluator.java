package com.hdh.band_project.permission;

import com.hdh.band_project.band.BandMember;
import com.hdh.band_project.band.BandMemberService;
import com.hdh.band_project.user.CustomUserDetails;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class LeaderPermissionEvaluator {
    private final BandMemberService bandMemberService;
    private final UserService userService;

    public boolean isLeader(Long id, Authentication authentication){
        System.out.println("🧪 id = " + id); // null인지 확인
        System.out.println("🧪 auth = " + authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return false;
        }

        SiteUser user = userService.getUser(userDetails.getUsername());
        BandMember bandMember = bandMemberService.getBandMember(user, id);
        if(bandMember==null){
            return false;
        }
        return bandMemberService.isLeader(bandMember);
    }

}
