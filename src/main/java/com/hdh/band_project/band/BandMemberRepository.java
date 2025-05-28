package com.hdh.band_project.band;

import com.hdh.band_project.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BandMemberRepository extends JpaRepository<BandMember, Long> {
    Optional<BandMember> findByUserAndBandId(SiteUser user, Long bandId);
    List<BandMember> findByMemberRoleAndUserId(MemberRole memberRole, Long userId);
    void deleteById(Long id);
    Optional<BandMember> findTopByBandOrderByCreatedAtAsc(Band band);
    Optional<BandMember> findByBandIdAndMemberRole(Long bandId, MemberRole memberRole);
    long countByBandIdAndMemberRoleNot(Long bandId, MemberRole memberRole);
    boolean existsByBandIdAndMemberRole(Long bandId, MemberRole memberRole);
}
