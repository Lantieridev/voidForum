package com.voidforum.service;

import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.model.Comment;
import com.voidforum.model.Post;
import com.voidforum.model.User;
import com.voidforum.repository.CommentRepository;
import com.voidforum.repository.PostRepository;
import com.voidforum.repository.UserRepository;
import com.voidforum.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public CommentResponseDto createComment(CommentCreateDto request, String username) {
        if (!postRepository.existsById(request.getPostId())) {
            throw new RuntimeException("Error: El post al que intentás comentar no existe.");
        }

        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Error: Comentario padre no encontrado."));
        }

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .postId(request.getPostId())
                .parentCommentId(request.getParentCommentId())
                .authorId(author.getId())
                .authorUsername(author.getUsername())
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        if (saved.getParentCommentId() == null || saved.getParentCommentId().isEmpty()) {
            postRepository.findById(request.getPostId()).ifPresent(post -> {
                post.setCommentCount(post.getCommentCount() != null ? post.getCommentCount() + 1 : 1);
                postRepository.save(post);
            });
        }

        return mapToResponseDto(saved, author.getId());
    }

    public List<CommentResponseDto> getCommentsByPost(String postId, String userId) {
        List<Comment> allComments = commentRepository.findByPostId(postId);

        List<Comment> rootComments = allComments.stream()
                .filter(c -> c.getParentCommentId() == null || c.getParentCommentId().isEmpty())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());

        return rootComments.stream()
                .map(c -> mapToResponseDto(c, userId))
                .collect(Collectors.toList());
    }

    public void deleteComment(String commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!comment.getAuthorUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para borrar este comentario");
        }

        String postId = comment.getPostId();
        boolean isRootComment = comment.getParentCommentId() == null || comment.getParentCommentId().isEmpty();

        commentRepository.deleteAllByParentCommentId(commentId);
        commentRepository.deleteById(commentId);

        if (isRootComment) {
            postRepository.findById(postId).ifPresent(post -> {
                int newCount = post.getCommentCount() != null ? Math.max(0, post.getCommentCount() - 1) : 0;
                post.setCommentCount(newCount);
                postRepository.save(post);
            });
        }
    }

    private CommentResponseDto mapToResponseDto(Comment comment, String userId) {
        List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());

        int voteCount = voteRepository.findAllByTargetIdAndTargetType(comment.getId(), "comment")
                .stream()
                .filter(v -> v.getValue() == 1)
                .mapToInt(v -> v.getValue())
                .sum();

        int userVote = 0;
        if (userId != null) {
            userVote = voteRepository.findByUserIdAndTargetIdAndTargetType(userId, comment.getId(), "comment")
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

    public CommentResponseDto updateComment(String id, CommentCreateDto commentRequest, String currentUsername) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!comment.getAuthorUsername().equals(currentUsername)) {
            throw new RuntimeException("No tenés permiso para editar este comentario");
        }

        comment.setContent(commentRequest.getContent());
        Comment updatedComment = commentRepository.save(comment);

        User author = userRepository.findByUsername(currentUsername).orElse(null);
        String authorId = author != null ? author.getId() : null;
        return mapToResponseDto(updatedComment, authorId);
    }
}