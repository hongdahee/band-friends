package com.hdh.band_project.user;

import com.hdh.band_project.band.BandMember;
import com.hdh.band_project.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private String profileImg;

    @Column(unique = true, nullable = false)
    private String uniqueId;

    private String regionName;

    private Double latitude;

    private Double longitude;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandMember> bandList;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
