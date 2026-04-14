package com.voidforum.service;

import com.voidforum.model.Comment;
import com.voidforum.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public List<Comment> getCommentsByPost(String postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @Override
    public String getUserVote(String commentId, String userId) { return ""; }

    @Override
    public Comment createComment(String postId, String content, String userId) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setAuthorId(userId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpvotes(0);
        comment.setDownvotes(0);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(String id, String userId) { commentRepository.deleteById(id); }

    @Override
    public VoteResult vote(String id, String type, String userId) { return new VoteResult(0, 0, type); }
}