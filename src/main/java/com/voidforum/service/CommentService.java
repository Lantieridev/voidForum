package com.voidforum.service;

import com.voidforum.model.Comment;
import com.voidforum.model.Post;
import com.voidforum.model.User;
import com.voidforum.model.Vote;
import com.voidforum.repository.CommentRepository;
import com.voidforum.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final PostService postService;
    private final UserService userService;

    public Comment createComment(String postId, String content, String userId) {
        Post post = postService.getPostById(postId);
        User user = userService.getUserById(userId);

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(userId);
        comment.setAuthorUsername(user.getUsername());
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setUpvotes(0);
        comment.setDownvotes(0);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(String postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    public Comment getCommentById(String id) {
        return commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public Comment updateComment(String id, String content, String userId) {
        Comment comment = getCommentById(id);
        
        if (!comment.getAuthorId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this comment");
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public void deleteComment(String id, String userId) {
        Comment comment = getCommentById(id);
        
        if (!comment.getAuthorId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public record VoteResult(int upvotes, int downvotes, String userVote) {}

    public VoteResult vote(String commentId, String voteType, String userId) {
        Comment comment = getCommentById(commentId);
        
        Optional<Vote> existingVote = voteRepository.findByUserIdAndTargetIdAndTargetType(userId, commentId, "comment");
        
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            
            if (vote.getVoteType().equals(voteType)) {
                voteRepository.delete(vote);
                updateCommentVotes(comment);
                return new VoteResult(comment.getUpvotes(), comment.getDownvotes(), null);
            }
            
            vote.setVoteType(voteType);
            voteRepository.save(vote);
            updateCommentVotes(comment);
        } else {
            Vote vote = new Vote();
            vote.setUserId(userId);
            vote.setTargetId(commentId);
            vote.setTargetType("comment");
            vote.setVoteType(voteType);
            vote.setCreatedAt(LocalDateTime.now());
            voteRepository.save(vote);
            updateCommentVotes(comment);
        }

        return new VoteResult(comment.getUpvotes(), comment.getDownvotes(), voteType);
    }

    private void updateCommentVotes(Comment comment) {
        int upvotes = (int) voteRepository.countByTargetIdAndVoteType(comment.getId(), "up");
        int downvotes = (int) voteRepository.countByTargetIdAndVoteType(comment.getId(), "down");
        comment.setUpvotes(upvotes);
        comment.setDownvotes(downvotes);
        commentRepository.save(comment);
    }

    public String getUserVote(String commentId, String userId) {
        return voteRepository.findByUserIdAndTargetIdAndTargetType(userId, commentId, "comment")
                .map(Vote::getVoteType)
                .orElse(null);
    }
}