package com.hdh.band_project.post;

import com.hdh.band_project.band.*;
import com.hdh.band_project.heart.HeartService;
import com.hdh.band_project.media.AwsS3Service;
import com.hdh.band_project.reply.Reply;
import com.hdh.band_project.reply.ReplyForm;
import com.hdh.band_project.reply.ReplyService;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/{category}")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private final ReplyService replyService;
    private final BandService bandService;
    private final BandMemberService bandMemberService;
    private final HeartService heartService;

    @GetMapping("/list")
    public String list(Model model, @PathVariable("category") String category, Principal principal,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "position", defaultValue = "all") String position,
                       @RequestParam(value = "genre", defaultValue = "ALL") Genre genre){
        List<Position> positionList = Arrays.asList(Position.values());
        model.addAttribute("positionList", positionList);
        model.addAttribute("selectedPosition", position);


        List<Genre> genreList = Arrays.asList(Genre.values());
        model.addAttribute("genreList", genreList);
        model.addAttribute("selectedGenre", genre);

        if(category.equalsIgnoreCase(Category.COMMUNITY.name())){
            Page<Post> paging = postService.searchCommunityPosts(kw, page);
            model.addAttribute("paging", paging);
        }
        else if(category.equalsIgnoreCase(Category.RECRUITMENT.name())) {
            if (principal == null) {
                return "redirect:/user/login";
            }
            SiteUser user = userService.getUser(principal.getName());

            model.addAttribute("paging", postService.searchRecruitPosts(kw,
                    page, user.getLatitude(), user.getLongitude(), position, genre));
        }
        model.addAttribute("kw", kw);
        return "post_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{bandId}/list")
    public String bandPostList(Model model, @PathVariable("category") String category,
                               @PathVariable("bandId") Long bandId,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "kw", defaultValue = "") String kw){
        Page<Post> paging = postService.searchBandPosts(kw, page, bandId);
        model.addAttribute("kw", kw);
        model.addAttribute("paging", paging);
        model.addAttribute("bandId", bandId);
        return "post_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(PostForm postForm, @PathVariable("category") String category,
                         Model model, Principal principal,
                         @RequestParam(value = "bandId", required = false) Long bandId){
        SiteUser user = userService.getUser(principal.getName());

        model.addAttribute("category", category);

        if(category.equalsIgnoreCase(Category.RECRUITMENT.name())){
            List<Band> userBandList = bandService.getBandWithMemberRole(user);
            List<Position> positionList = Arrays.asList(Position.values());
            model.addAttribute("bandList", userBandList);
            model.addAttribute("positionList", positionList);
        }
        if(category.equalsIgnoreCase(Category.BAND.name())) {
            model.addAttribute("bandId", bandId);
        }
        return "post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid PostForm postForm, BindingResult bindingResult,
                         Principal principal, @PathVariable("category") String category,
                         @RequestParam(value = "bandId", required = false) Long bandId,
                         Model model){
        SiteUser user = userService.getUser(principal.getName());

        if (bindingResult.hasErrors()){
            if(category.equalsIgnoreCase(Category.RECRUITMENT.name())){
                List<Band> userBandList = bandService.getBandWithMemberRole(user);
                List<Position> positionList = Arrays.asList(Position.values());
                model.addAttribute("bandList", userBandList);
                model.addAttribute("positionList", positionList);
            }
            return "post_form";
        }

        Boolean isClosed = null;
        Band band = null;
        List<Position> position = null;
        Boolean isPrivate = false;

        if(category.equalsIgnoreCase(Category.RECRUITMENT.name())){
            if (postForm.getPosition() == null || postForm.getPosition().isEmpty()) {
                bindingResult.rejectValue("position", "required", "하나 이상의 포지션을 선택해주세요.");
            }
            band = postForm.getBand();
            BandMember bandMember = bandMemberService.getBandMember(user, band.getId());
            bandMemberService.checkLeaderOrStaff(bandMember);

            isClosed = false;
            position = postForm.getPosition();
        }
        if(category.equalsIgnoreCase(Category.BAND.name())){
            band = bandService.getBand(bandId);
            isPrivate = postForm.getIsPrivate();
        }

        Long postId = postService.create(postForm.getSubject(), postForm.getContent(),
                user, category, isClosed, band, isPrivate, position);
        Post post = postService.getPost(postId);
        List<MultipartFile> mediaList = postForm.getMediaList();

        if (mediaList != null && !mediaList.isEmpty()) {
            awsS3Service.uploadMedia(post, mediaList);
        }

        return "redirect:/{category}/detail/" + postId;
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long postId, HttpServletRequest request,
                         HttpServletResponse response, @PathVariable("category") String category,
                         ReplyForm replyForm, Principal principal){
        Post post = postService.getPost(postId);
        model.addAttribute("isVisible", false);
        if(category.equalsIgnoreCase(Category.BAND.name())){
            if(principal==null){
                return "redirect:/user/login";
            }
            SiteUser user = userService.getUser(principal.getName());
            Boolean isBandMember = bandMemberService.isBandMember(user, post.getBand().getId());
            if(!post.getIsPrivate() || isBandMember){
                model.addAttribute("isVisible", true);
            }
        }
        else{
            model.addAttribute("isVisible", true);
        }

        validateView(postId, request, response);

        List<Reply> replyList = replyService.getParentReplies(postId);

        model.addAttribute("post", post);
        model.addAttribute("category", category);
        model.addAttribute("replyList", replyList);

        return "post_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(PostForm postForm, @PathVariable("id") Long id, Principal principal,
                         Model model, @PathVariable("category") String category){
        model.addAttribute("category", category);
        model.addAttribute("modify", true);

        Post post = postService.getPost(id);
        if(!post.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        model.addAttribute("post", post);

        postForm.setMediaList(null);

        postForm.setSubject(post.getSubject());
        postForm.setContent(post.getContent());
        postForm.setPosition(post.getPositionList());
        postForm.setIsPrivate(post.getIsPrivate());
        postForm.setBand(post.getBand());
        postForm.setIsClosed(post.getIsClosed());

        if(category.equals("recruitment")) {
            SiteUser user = userService.getUser(principal.getName());

            List<Band> userBandList = userService.getUserBandList(user);

            List<Position> positionList = Arrays.asList(Position.values());

            model.addAttribute("bandList", userBandList);
            model.addAttribute("positionList", positionList);
        }
        return "post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@Valid PostForm postForm, BindingResult bindingResult,
                         Principal principal, @PathVariable("id") Long id,
                         @PathVariable("category") String category){
        if(bindingResult.hasErrors()){
            return "post_form";
        }
        Post post = postService.getPost(id);
        if(!post.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        if(!category.equals("recruitment")){
            postForm.setIsClosed(false);
        }
        if(!category.equals("band")){
            postForm.setIsPrivate(false);
        }
        postService.modify(post, postForm.getSubject(), postForm.getContent(),
                postForm.getPosition(), postForm.getIsClosed(), postForm.getIsPrivate());

        List<MultipartFile> mediaList = postForm.getMediaList();

        if (mediaList != null && !mediaList.isEmpty()) {
            awsS3Service.uploadMedia(post, mediaList);
        }

        return "redirect:/{category}/detail/"+id;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(Principal principal, @PathVariable("id") Long id){
        Post post = postService.getPost(id);
        if(!post.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        postService.delete(post);
        return "redirect:/{category}/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like/{id}")
    public String like(Principal principal, @PathVariable("id") Long id){
        Post post = postService.getPost(id);
        SiteUser user = userService.getUser(principal.getName());
        heartService.like(post, user);
        return "redirect:/{category}/detail/"+id;
    }

    private void validateView(Long postId, HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElseGet(() -> new Cookie[0]);

        Cookie cookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("postView"))
                .findFirst()
                .orElseGet(() -> {
                    postService.addView(postId);
                    return new Cookie("postView", "[" + postId + "]");
                });

        if(!cookie.getValue().contains("[" + postId + "]")){
            postService.addView(postId);
            cookie.setValue(cookie.getValue() + "[" + postId + "]");
        }
        // 쿠키 유지 시간 설정
        long todayEndSecond = LocalDate.now().atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC);
        long currentSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        cookie.setPath("/");
        cookie.setMaxAge((int) (todayEndSecond - currentSecond));
        response.addCookie(cookie);
    }
}
