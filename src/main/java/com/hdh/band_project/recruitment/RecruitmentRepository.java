package com.hdh.band_project.recruitment;

import com.hdh.band_project.band.Band;
import com.hdh.band_project.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<MemberRecruitment, Long> {
    public List<MemberRecruitment> findByAppliedBand(Band band);
    public long countByAppliedBand(Band band);
    public Optional<MemberRecruitment> findByAppliedBandAndUser(Band band, SiteUser user);
    public void deleteByAppliedBand(Band band);
    public void deleteByUserId(Long userId);
}
