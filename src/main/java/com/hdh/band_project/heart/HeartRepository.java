package com.hdh.band_project.heart;

import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    public Heart findByPostAndVoter(Post post, SiteUser voter);
}
