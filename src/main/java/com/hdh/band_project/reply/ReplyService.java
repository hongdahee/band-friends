package com.hdh.band_project.reply;

import com.hdh.band_project.DataNotFoundException;
import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;

    public Reply getReply(Long id){
        Optional<Reply> reply = replyRepository.findById(id);
        if(reply.isPresent()){
            return reply.get();
        }
        else{
            throw new DataNotFoundException("reply not found");
        }
    }

    private Reply reply(String content, Post post, SiteUser user){
        Reply reply = new Reply();
        reply.setPost(post);
        reply.setAuthor(user);
        reply.setContent(content);
        reply.setCreatedAt(LocalDateTime.now());
        return reply;
    }

    public void save(String content, Post post, SiteUser user){
        Reply reply = reply(content, post, user);
        replyRepository.save(reply);
    }

    public void modify(Reply reply, String content){
        reply.setContent(content);
        reply.setModifiedAt(LocalDateTime.now());
        replyRepository.save(reply);
    }

    public void delete(Reply reply){
        replyRepository.delete(reply);
    }

    public void response(Reply parentReply, String content, Post post, SiteUser user){
        Reply reply = reply(content, post, user);
        reply.setParent(parentReply);
        replyRepository.save(reply);
    }

    // 대댓글이 아닌 일반 댓글만 불러오는 메서드
    public List<Reply> getParentReplies(Long postId) {
        return replyRepository.findByPostIdAndParentIsNull(postId);
    }

    public void deleteRepliesByUserId(Long userId){
        replyRepository.deleteByAuthorId(userId);
    }
}
