package com.hdh.band_project.recruitment;

import com.hdh.band_project.band.Band;
import com.hdh.band_project.post.Position;
import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class MemberRecruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SiteUser user;

    @Column(nullable = false)
    private Position position;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Band appliedBand;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;
}
