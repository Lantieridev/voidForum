package com.voidforum.service;

import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.model.Comment;
import com.voidforum.model.User;
import com.voidforum.repository.CommentRepository;
import com.voidforum.repository.PostRepository;
import com.voidforum.repository.UserRepository;
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

    public CommentResponseDto createComment(CommentCreateDto request, String username) {
        if (!postRepository.existsById(request.postId())) {
            throw new RuntimeException("Error: El post al que intentas comentar no existe.");
        }

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

        Comment comment = Comment.builder()
                .content(request.content())
                .postId(request.postId())
                .authorId(author.getId())
                .authorUsername(author.getUsername())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponseDto(commentRepository.save(comment));
    }

    public List<CommentResponseDto> getCommentsByPost(String postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(String commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        // Seguridad: Solo el autor borra su comentario
        if (!comment.getAuthorUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para borrar este comentario");
        }

        commentRepository.deleteById(commentId);
    }

    private CommentResponseDto mapToResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorUsername(),
                comment.getPostId(),
                comment.getCreatedAt()
        );
    }
}