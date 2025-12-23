package com.hdh.band_project.recruitment;

import com.hdh.band_project.DataNotFoundException;
import com.hdh.band_project.band.Band;
import com.hdh.band_project.band.BandMemberService;
import com.hdh.band_project.band.BandService;
import com.hdh.band_project.band.MemberRole;
import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.CustomUserDetails;
import com.hdh.band_project.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final BandMemberService bandMemberService;

    public MemberRecruitment getMemberRecruitment(Long applyId){
        Optional<MemberRecruitment> memberRecruitment = recruitmentRepository.findById(applyId);
        if(memberRecruitment.isPresent()){
            return memberRecruitment.get();
        }
        else{
            throw new DataNotFoundException("memberRecruitment not found");
        }
    }

    public void apply(ApplyForm applyForm, Band band, SiteUser user){
        MemberRecruitment memberRecruitment = new MemberRecruitment();
        memberRecruitment.setAnswer(applyForm.getAnswer());
        memberRecruitment.setPosition(applyForm.getPosition());
        memberRecruitment.setAppliedBand(band);
        memberRecruitment.setUser(user);
        recruitmentRepository.save(memberRecruitment);
    }

    public List<MemberRecruitment> getApplyList(Band band){
        return recruitmentRepository.findByAppliedBand(band);
    }

    public void acceptApply(MemberRecruitment memberRecruitment){
        recruitmentRepository.save(memberRecruitment);
        bandMemberService.create(memberRecruitment.getPosition(), memberRecruitment.getAppliedBand(),
                memberRecruitment.getUser(), MemberRole.MEMBER);
        recruitmentRepository.delete(memberRecruitment);
    }

    public void rejectApply(MemberRecruitment memberRecruitment){
        recruitmentRepository.delete(memberRecruitment);
    }

    public Boolean hasApplied(Band band, SiteUser user){
        return recruitmentRepository.findByAppliedBandAndUser(band, user).isPresent();
    }

    public long getNewApplyCount(Band band){
        return recruitmentRepository.countByAppliedBand(band);
    }

    public void deleteByAppliedBand(Band band){
        recruitmentRepository.deleteByAppliedBand(band);
    }

    public void deleteByUserId(Long id) {
        recruitmentRepository.deleteByUserId(id);
    }
}
