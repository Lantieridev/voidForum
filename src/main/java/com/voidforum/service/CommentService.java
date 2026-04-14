package com.voidforum.service;

import com.voidforum.model.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByPost(String postId);
    String getUserVote(String commentId, String userId);
    Comment createComment(String postId, String content, String userId);
    void deleteComment(String id, String userId);
    VoteResult vote(String id, String type, String userId);

    // DTO para los votos de comentarios
    record VoteResult(int upvotes, int downvotes, String userVote) {}
}