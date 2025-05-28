package com.hdh.band_project.post;

import com.hdh.band_project.band.Band;
import com.hdh.band_project.heart.Heart;
import com.hdh.band_project.media.Media;
import com.hdh.band_project.reply.Reply;
import com.hdh.band_project.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length=200)
    private String subject;

    @ManyToOne
    private SiteUser author;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean isClosed = false; // (recruitment)

    @Column(nullable = false)
    private Boolean isPrivate = true; // (band)

    @ManyToOne
    private Band band; // (recruitment, band)

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_position", // 생성될 테이블 이름
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Enumerated(EnumType.STRING)
    private List<Position> positionList; // (recruitment)

    @Column(nullable = false)
    private Integer viewCount=0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Heart> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> mediaList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replyList = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public void addViewCount(){
        this.viewCount++;
    }
}
