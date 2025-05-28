package com.hdh.band_project.reply;

import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length=300)
    private String content;

    @ManyToOne
    private SiteUser author;

    @ManyToOne
    private Post post;

    @ManyToOne(optional = true)
    private Reply parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Reply> childrenList = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
