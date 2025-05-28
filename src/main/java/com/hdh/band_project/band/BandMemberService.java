package com.hdh.band_project.band;

import com.hdh.band_project.Authority;
import com.hdh.band_project.DataNotFoundException;
import com.hdh.band_project.post.Position;
import com.hdh.band_project.user.CustomUserDetails;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BandMemberService {

    private final BandMemberRepository bandMemberRepository;

    public void create(Position position, Band band,
                       SiteUser user, MemberRole memberRole){
        BandMember bandMember = new BandMember();
        bandMember.setPosition(position);
        bandMember.setBand(band);
        bandMember.setUser(user);
        bandMember.setMemberRole(memberRole);
        bandMember.setCreatedAt(LocalDateTime.now());
        bandMemberRepository.save(bandMember);
    }

    public BandMember getBandMember(SiteUser user, Long bandId){
        Optional<BandMember> bandMember = bandMemberRepository.findByUserAndBandId(user, bandId);
        if(bandMember.isPresent()){
            return bandMember.get();
        }
        else{
            throw new DataNotFoundException("bandMember not found");
        }
    }

    public BandMember getBandMemberById(Long bandMemberId){
        Optional<BandMember> bandMember = bandMemberRepository.findById(bandMemberId);
        if(bandMember.isPresent()){
            return bandMember.get();
        }
        else{
            throw new DataNotFoundException("bandMember not found");
        }
    }

    public Boolean isBandMember(SiteUser user, Long bandId){
        return bandMemberRepository.findByUserAndBandId(user, bandId).isPresent();
    }

    public boolean isLeader(BandMember bandMember){
        return bandMember.getMemberRole() == MemberRole.LEADER;
    }

    public boolean isStaff(BandMember bandMember){
        return bandMember.getMemberRole() == MemberRole.STAFF;
    }

    public List<Band> getBandsWithLeaderRole(Long userId){
        List<BandMember> bandMembers = bandMemberRepository.findByMemberRoleAndUserId(MemberRole.LEADER, userId);
        return bandMembers.stream()
                .map(BandMember::getBand)
                .collect(Collectors.toList());
    }

    public void quitBand(Long bandMemberId){
        bandMemberRepository.deleteById(bandMemberId);
    }

    public BandMember getFirstMember(Band band){
        Optional<BandMember> bandMember = bandMemberRepository.findTopByBandOrderByCreatedAtAsc(band);
        if(bandMember.isPresent()){
            return bandMember.get();
        }
        else{
           return null;
        }
    }

    public void changeMemberRole(BandMember bandMember, MemberRole memberRole){
        bandMember.setMemberRole(memberRole);
        bandMemberRepository.save(bandMember);
    }

    public Optional<BandMember> getStaff(Band band) {
        return bandMemberRepository.findByBandIdAndMemberRole(band.getId(), MemberRole.STAFF);
    }

    public Long getMemberCountExceptLeader(Long bandId){
        return bandMemberRepository.countByBandIdAndMemberRoleNot(bandId, MemberRole.LEADER);
    }

    public void modifyPosition(Long bandId, SiteUser user, Position position) {
        BandMember bandMember = getBandMember(user, bandId);
        if(position.name().equals(bandMember.getPosition().name())){
            return;
        }
        else {
            bandMember.setPosition(position);
        }
        bandMemberRepository.save(bandMember);
    }

    public void modifyStaffStatus(Long bandId, Long memberId, SiteUser loggedInUser) {
        BandMember loggedInMember = getBandMember(loggedInUser, bandId);
        if(checkLeader(loggedInMember)){
            BandMember bandMember = getBandMemberById(memberId);
            if(bandMember.getMemberRole()==MemberRole.STAFF){
                bandMember.setMemberRole(MemberRole.MEMBER);
            }
            else if(bandMember.getMemberRole()==MemberRole.MEMBER){
                if(!hasStaff(bandId)){
                    bandMember.setMemberRole(MemberRole.STAFF);
                }
            }
        }
    }

    public void changeLeader(Long bandId, Long memberId, SiteUser loggedInUser) {
        BandMember loggedInMember = getBandMember(loggedInUser, bandId);
        if(checkLeader(loggedInMember)){
            BandMember leader = getLeader(bandId);
            leader.setMemberRole(MemberRole.MEMBER);

            BandMember bandMember = getBandMemberById(memberId);
            bandMember.setMemberRole(MemberRole.LEADER);
        }
    }

    public void kickMember(Long bandId, Long memberId, SiteUser loggedInUser){
        BandMember loggedInMember = getBandMember(loggedInUser, bandId);
        if(checkLeader(loggedInMember)){
            bandMemberRepository.deleteById(memberId);
        }
    }

    private boolean hasStaff(Long bandId){
        return bandMemberRepository.existsByBandIdAndMemberRole(bandId, MemberRole.STAFF);
    }

    private BandMember getLeader(Long bandId){
        Optional<BandMember> bandMember = bandMemberRepository.findByBandIdAndMemberRole(bandId, MemberRole.LEADER);
        if(bandMember.isPresent()){
            return bandMember.get();
        }
        else{
            throw new DataNotFoundException("bandMember not found");
        }
    }

    public boolean checkLeader(BandMember bandMember){
        if(isLeader(bandMember)){
            return true;
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "리더 권한이 필요한 요청입니다.");
        }
    }

    public void checkLeaderOrStaff(BandMember bandMember){
        if(!isLeader(bandMember) && !isStaff(bandMember)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "리더 또는 스탭 권한이 필요한 요청입니다.");
        }
    }

    public boolean checkBandMember(SiteUser user, Long bandId){
        boolean isBandMember = isBandMember(user, bandId);
        if(isBandMember){
            return true;
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "밴드 멤버 권한이 필요한 요청입니다.");
        }
    }
}
