package com.hdh.band_project.post;

import com.hdh.band_project.DataNotFoundException;
import com.hdh.band_project.band.Band;
import com.hdh.band_project.band.Genre;
import com.hdh.band_project.media.AwsS3Service;
import com.hdh.band_project.media.Media;
import com.hdh.band_project.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final AwsS3Service awsS3Service;

    public Page<Post> getAllPosts(String category, int page){
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sortDesc()));
        return postRepository.findByCategory(category, pageable);
    }

    public Post getPost(Long id){
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }
        else{
            throw new DataNotFoundException("post not found");
        }
    }

    public List<Post> getPostsByAuthor(SiteUser user){
        return postRepository.findByAuthor(user);
    }

    public List<Post> getPostsByBand(Band band){
        return postRepository.findByBand(band);
    }

    public Page<Post> getPostsWithin30km(SiteUser user, int page){
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository.findPostsWithin30km(user.getLatitude(), user.getLongitude(), pageable);
    }

    public Long create(String subject, String content, SiteUser author,
                       String category, Boolean isClosed, Band band,
                       Boolean isPrivate, List<Position> positionList){
        Post post = new Post();
        post.setSubject(subject);
        post.setContent(content);
        post.setAuthor(author);
        post.setCategory(category);
        if(category.equalsIgnoreCase(Category.RECRUITMENT.name())){
            post.setIsClosed(isClosed);
            post.setPositionList(positionList);
        }
        if(category.equalsIgnoreCase(Category.BAND.name())){
            post.setIsPrivate(isPrivate);
        }

        if(category.equalsIgnoreCase(Category.BAND.name()) || category.equalsIgnoreCase(Category.RECRUITMENT.name())){
            post.setBand(band);
        }
        post.setCreatedAt(LocalDateTime.now());
        Long postId = postRepository.save(post).getId();
        return postId;
    }

    public void modify(Post post, String subject, String content,
                       List<Position> positionList, Boolean isClosed,
                       Boolean isPrivate){
        post.setSubject(subject);
        post.setContent(content);
        post.setPositionList(positionList);
        post.setIsClosed(isClosed);
        post.setIsPrivate(isPrivate);
        post.setModifiedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public void addView(Long postId){
        Post post = getPost(postId);
        post.addViewCount();
        postRepository.save(post);
    }

    public void delete(Post post){
        postRepository.delete(post);

        List<Media> mediaList = post.getMediaList();
        for(Media media : mediaList){
            awsS3Service.deleteFile(media.getFilePath());
        }
    }

    public Page<Post> getBandPosts(Long bandId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sortDesc()));
        return postRepository.findByBandIdAndCategory(bandId, Category.BAND.name(), pageable);
    }

    public Page<Post> searchCommunityPosts(String keyword, int page){
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sortDesc()));
        return postRepository.searchCommunityPosts(Category.COMMUNITY.name(), keyword, pageable);
    }

    public Page<Post> searchBandPosts(String keyword, int page, Long bandId){
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sortDesc()));
        return postRepository.searchBandPosts(bandId, Category.BAND.name(), keyword, pageable);
    }

    public Page<Post> searchRecruitPosts(String keyword, int page,
                                         double userLat, double userLon, String position, Genre genre){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("created_at"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.searchRecruitPostsWithin30km(Category.RECRUITMENT.name(), userLat, userLon, keyword, position, genre, pageable);
    }


    public void deleteByBand(Band band){
        postRepository.deleteByBand(band);
    }

    public void deleteByBandAndAuthor(Band band, SiteUser author){
        postRepository.deleteByBandAndAuthor(band, author);
    }

    public boolean isClosed(Post post){
        return post.getIsClosed();
    }

    private List<Sort.Order> sortDesc(){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        return sorts;
    }
}
