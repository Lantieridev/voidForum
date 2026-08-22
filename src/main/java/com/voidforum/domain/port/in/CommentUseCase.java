package com.voidforum.domain.port.in;

import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;

import java.util.List;

public interface CommentUseCase {
    CommentResponseDto createComment(CommentCreateDto request, String username);
    List<CommentResponseDto> getCommentsByPost(String postId, String userId);
    void deleteComment(String commentId, String username);
    void anonymizeUserComments(String oldUsername, String newUsername);
    CommentResponseDto updateComment(String id, CommentCreateDto commentRequest, String currentUsername);
}
