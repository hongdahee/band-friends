package com.hdh.band_project.band;

import com.hdh.band_project.post.Position;
import com.hdh.band_project.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class BandMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Position position;

    @ManyToOne
    private Band band;

    @ManyToOne
    private SiteUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
