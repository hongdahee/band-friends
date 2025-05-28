package com.hdh.band_project.heart;

import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "heart")
public class Heart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action= OnDeleteAction.CASCADE)
    private SiteUser voter;

    @ManyToOne
    private Post post;
}
