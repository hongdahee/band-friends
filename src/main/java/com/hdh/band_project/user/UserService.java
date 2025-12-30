package com.hdh.band_project.user;

import com.hdh.band_project.DataNotFoundException;
import com.hdh.band_project.band.Band;
import com.hdh.band_project.band.BandMember;
import com.hdh.band_project.media.AwsS3Service;
import com.hdh.band_project.media.Media;
import com.hdh.band_project.post.Post;
import com.hdh.band_project.post.PostService;
import com.hdh.band_project.recruitment.RecruitmentService;
import com.hdh.band_project.reply.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hdh.band_project.util.UniqueIdGenerator.generateUniqueId;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;
    private final ReplyService replyService;
    private final PostService postService;
    private final RecruitmentService recruitmentService;

    public SiteUser create(String username, String email, String password, String nickname){
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);

        String uniqueId = generateUniqueId(userRepository);
        user.setUniqueId(uniqueId);
        userRepository.save(user);
        return user;
    }

    public String validate(String username, String email){
        boolean duplicatedUsername = userRepository.existsByUsername(username);
        boolean duplicatedEmail = userRepository.existsByEmail(email);
        if(duplicatedUsername & duplicatedEmail){
            return "all";
        }
        else if(duplicatedUsername){
            return "username";
        }
        else if(duplicatedEmail){
            return "email";
        }
        return "";
    }

    public SiteUser getUser(String username){
        Optional<SiteUser> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            return user.get();
        }
        else{
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser getUserByUniqueId(String uniqueId){
        Optional<SiteUser> user = userRepository.findByUniqueId(uniqueId);
        if(user.isPresent()){
            return user.get();
        }
        else{
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public List<Band> getUserBandList(SiteUser user){
        return user.getBandList().stream()
                .map(BandMember::getBand)
                .collect(Collectors.toList());
    }

    public Boolean checkUserExistsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public SiteUser getUserByEmail(String email){
        Optional<SiteUser> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            return user.get();
        }
        else{
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public void editUserInfo(MultipartFile profile, SiteUser user){
        String userProfile = user.getProfileImg();
        String profileUrl = awsS3Service.uploadProfileImg(profile);
        if(profileUrl!=null && !profileUrl.isBlank()) {
            user.setProfileImg(profileUrl);
            userRepository.save(user);
//            if(userProfile!=null){
//                awsS3Service.deleteFile(userProfile);
//            }
        }
    }

    public void editNickname(String nickname, SiteUser user) {
        user.setNickname(nickname);
        userRepository.save(user);
    }

    public void delete(SiteUser user, HttpServletRequest request, HttpServletResponse response) {
        replyService.deleteRepliesByUserId(user.getId());
        recruitmentService.deleteByUserId(user.getId());
        userRepository.delete(user);
        userImgDelete(user);
        logout(request, response);
    }

    private void userImgDelete(SiteUser user){
        String profileUrl = user.getProfileImg();
//        if(profileUrl!=null) {
//            awsS3Service.deleteFile(user.getProfileImg());
//        }

//        for (Post post : postService.getPostsByAuthor(user)) {
//            for (Media media : post.getMediaList()) {
//                awsS3Service.deleteFile(media.getFilePath());
//            }
//        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response){
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }

    public void autoLogin(String email, HttpServletRequest request){
        SiteUser user = getUserByEmail(email);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        HttpSession session = request.getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

    public void saveLocation(SiteUser user, String regionName, Double lat, Double lon){
        user.setRegionName(regionName);
        user.setLatitude(lat);
        user.setLongitude(lon);
        userRepository.save(user);
    }
}
