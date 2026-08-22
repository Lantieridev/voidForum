package com.voidforum.domain.service;

import com.voidforum.domain.model.Comment;
import com.voidforum.domain.model.User;
import com.voidforum.domain.port.in.CommentUseCase;
import com.voidforum.domain.port.out.CommentRepositoryPort;
import com.voidforum.domain.port.out.PostRepositoryPort;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.domain.port.out.VoteRepositoryPort;
import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.exception.ForbiddenException;
import com.voidforum.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentUseCase {

    private final CommentRepositoryPort commentRepositoryPort;
    private final PostRepositoryPort postRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final VoteRepositoryPort voteRepositoryPort;

    @Override
    public CommentResponseDto createComment(CommentCreateDto request, String username) {
        if (postRepositoryPort.findById(request.getPostId()).isEmpty()) {
            throw new ResourceNotFoundException("Error: El post al que intentás comentar no existe.");
        }

        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            commentRepositoryPort.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Comentario padre no encontrado."));
        }

        User author = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Usuario no encontrado."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .postId(request.getPostId())
                .parentCommentId(request.getParentCommentId())
                .authorId(author.getId())
                .authorUsername(author.getUsername())
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepositoryPort.save(comment);

        if (saved.getParentCommentId() == null || saved.getParentCommentId().isEmpty()) {
            postRepositoryPort.findById(request.getPostId()).ifPresent(post -> {
                post.setCommentCount(post.getCommentCount() != null ? post.getCommentCount() + 1 : 1);
                postRepositoryPort.save(post);
            });
        }

        return mapToResponseDto(saved, author.getId());
    }

    @Override
    public List<CommentResponseDto> getCommentsByPost(String postId, String userId) {
        List<Comment> allComments = commentRepositoryPort.findByPostId(postId);

        List<Comment> rootComments = allComments.stream()
                .filter(c -> c.getParentCommentId() == null || c.getParentCommentId().isEmpty())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());

        return rootComments.stream()
                .map(c -> mapToResponseDto(c, userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(String commentId, String username) {
        Comment comment = commentRepositoryPort.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));

        if (!comment.getAuthorUsername().equals(username)) {
            throw new ForbiddenException("No tienes permiso para borrar este comentario");
        }

        String postId = comment.getPostId();
        boolean isRootComment = comment.getParentCommentId() == null || comment.getParentCommentId().isEmpty();

        commentRepositoryPort.deleteAllByParentCommentId(commentId);
        commentRepositoryPort.deleteById(commentId);

        if (isRootComment) {
            postRepositoryPort.findById(postId).ifPresent(post -> {
                int newCount = post.getCommentCount() != null ? Math.max(0, post.getCommentCount() - 1) : 0;
                post.setCommentCount(newCount);
                postRepositoryPort.save(post);
            });
        }
    }

    @Override
    public void anonymizeUserComments(String oldUsername, String newUsername) {
        List<Comment> comments = commentRepositoryPort.findByAuthorUsername(oldUsername);
        for (Comment comment : comments) {
            comment.setAuthorUsername(newUsername);
        }
        commentRepositoryPort.saveAll(comments);
    }

    @Override
    public CommentResponseDto updateComment(String id, CommentCreateDto commentRequest, String currentUsername) {
        Comment comment = commentRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));

        if (!comment.getAuthorUsername().equals(currentUsername)) {
            throw new ForbiddenException("No tenés permiso para editar este comentario");
        }

        comment.setContent(commentRequest.getContent());
        Comment updatedComment = commentRepositoryPort.save(comment);

        User author = userRepositoryPort.findByUsername(currentUsername).orElse(null);
        String authorId = author != null ? author.getId() : null;
        return mapToResponseDto(updatedComment, authorId);
    }

    private CommentResponseDto mapToResponseDto(Comment comment, String userId) {
        List<Comment> replies = commentRepositoryPort.findByParentCommentId(comment.getId());

        int voteCount = voteRepositoryPort.findAllByTargetIdAndTargetType(comment.getId(), "comment")
                .stream()
                .filter(v -> v.getValue() == 1)
                .mapToInt(v -> v.getValue())
                .sum();

        int userVote = 0;
        if (userId != null) {
            userVote = voteRepositoryPort.findByUserIdAndTargetIdAndTargetType(userId, comment.getId(), "comment")
                    .map(v -> v.getValue())
                    .orElse(0);
        }

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorUsername(),
                comment.getPostId(),
                comment.getParentCommentId(),
                comment.getCreatedAt(),
                voteCount,
                userVote,
                replies.stream().map(r -> mapToResponseDto(r, userId)).collect(Collectors.toList())
        );
    }
}
