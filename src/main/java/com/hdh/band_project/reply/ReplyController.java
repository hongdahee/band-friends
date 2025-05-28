package com.hdh.band_project.reply;

import com.hdh.band_project.post.Post;
import com.hdh.band_project.post.PostForm;
import com.hdh.band_project.post.PostService;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {
    private final ReplyService replyService;
    private final PostService postService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}")
    public String reply(@PathVariable("postId") Long postId, @Valid ReplyForm replyForm,
                        BindingResult bindingResult, Principal principal){
        Post post = postService.getPost(postId);
        String content = replyForm.getContent();
        SiteUser author = userService.getUser(principal.getName());
        String category = post.getCategory();

        replyService.save(content, post, author);
        return String.format("redirect:/%s/detail/%d", category, postId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{replyId}")
    public String modify(@PathVariable("replyId") Long replyId,
                         @Valid ReplyForm replyForm,
                         BindingResult bindingResult, Principal principal){
        Reply reply = replyService.getReply(replyId);
        Post post = reply.getPost();
        String category = post.getCategory();
        String content = replyForm.getContent();

        if(!reply.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        replyService.modify(reply, content);
        return String.format("redirect:/%s/detail/%d", category, post.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/remove/{replyId}")
    public String delete(Principal principal, @PathVariable("replyId") Long replyId){
        Reply reply = replyService.getReply(replyId);
        Post post = reply.getPost();
        String category = post.getCategory();

        if(!reply.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        replyService.delete(reply);
        return String.format("redirect:/%s/detail/%d", category, post.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/response/{replyId}")
    public String response(@PathVariable("replyId") Long replyId, @Valid ReplyForm replyForm,
                        BindingResult bindingResult, Principal principal){
        Reply reply = replyService.getReply(replyId);
        Post post = reply.getPost();
        String content = replyForm.getContent();
        String category = post.getCategory();
        SiteUser loggedInUser = userService.getUser(principal.getName());

        replyService.response(reply, content, post, loggedInUser);
        return String.format("redirect:/%s/detail/%d", category, post.getId());
    }

}
