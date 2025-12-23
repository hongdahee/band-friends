package com.hdh.band_project.post;

import com.hdh.band_project.band.Band;
import com.hdh.band_project.band.BandService;
import com.hdh.band_project.band.Genre;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
//@Transactional
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class PostServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private BandService bandService;

    @Test
    void findPostsWithin30km(){
        SiteUser user1 = userService.create("testuser", "test@test.com", "testuser1!", "tester");
        SiteUser user2 = userService.create("testuser2", "test2@test.com", "testuser2!", "tester2");
        SiteUser user3 = userService.create("testuser3", "test3@test.com", "testuser3!", "tester3");

        Double busanLat = 35.1796;
        Double busanLon = 129.0756;

        userService.saveLocation(user1, "부산", busanLat, busanLon);

        Long nearBandId = bandService.create("Busan band", Genre.ROCK, "question",
                "부산", 35.1800, 129.0800);

        Band nearBand = bandService.getBand(nearBandId);

        List<Position> positions = List.of(Position.VOCAL, Position.GUITAR);

        postService.create("부산 밴드가 올린 글", "내용", user2,
                Category.RECRUITMENT.name(), false, nearBand, true, positions);

        Long farBandId = bandService.create("Seoul band", Genre.ROCK, "question",
                "서울", 37.5665, 126.9780);

        Band farBand = bandService.getBand(farBandId);

        postService.create("서울 밴드가 올린 글", "내용", user3,
                Category.RECRUITMENT.name(), false, farBand, true, positions);

//        List<Post> result = postService.getPostsWithin30km(user1);
//
//        assertEquals(1, result.size());
//        assertEquals("부산 밴드가 올린 글", result.getFirst().getSubject());
    }

}
