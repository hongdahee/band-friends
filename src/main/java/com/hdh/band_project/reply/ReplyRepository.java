package com.hdh.band_project.reply;

import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPost(Post post);
    List<Reply> findByPostIdAndParentIsNull(Long postId);
    void deleteByAuthorId(Long authorId);
}
