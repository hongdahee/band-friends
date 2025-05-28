package com.hdh.band_project.heart;

import com.hdh.band_project.DataNotFoundException;
import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;

    public Heart getLike(Post post, SiteUser user){
        return heartRepository.findByPostAndVoter(post, user);
    }

    public void like(Post post, SiteUser user) {
        Heart existingHeart = getLike(post, user);
        if(existingHeart!=null){
            delete(existingHeart);
        }
        else {
            Heart heart = new Heart();
            heart.setPost(post);
            heart.setVoter(user);
            heartRepository.save(heart);
        }
    }

    private void delete(Heart heart){
        heartRepository.delete(heart);
    }

}
