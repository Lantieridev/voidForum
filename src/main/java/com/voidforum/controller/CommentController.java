package com.voidforum.controller;

import com.voidforum.model.Comment;
import com.voidforum.service.CommentService;
import com.voidforum.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;

    // --- DTO (Records) para mandar y recibir datos ---
    public record CreateCommentRequest(String content) {}

    // El molde seguro para devolver comentarios sin que exploten los nulos
    public record CommentResponse(
            String id, String postId, String authorId, String authorUsername,
            String content, String createdAt, int upvotes, int downvotes, String userVote
    ) {
        public static CommentResponse fromComment(Comment comment, String userVote) {
            return new CommentResponse(
                    comment.getId(),
                    comment.getPostId(),
                    comment.getAuthorId(),
                    comment.getAuthorUsername(),
                    comment.getContent(),
                    comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : "",
                    comment.getUpvotes(),
                    comment.getDownvotes(),
                    userVote != null ? userVote : ""
            );
        }
    }

    // --- ENDPOINTS ---

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable String postId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String userId = getUserIdIfAuthenticated(authHeader);

        List<Comment> comments = commentService.getCommentsByPost(postId);

        // Mapeamos usando nuestro record seguro
        List<CommentResponse> response = comments.stream().map(comment -> {
            String userVote = userId != null ? commentService.getUserVote(comment.getId(), userId) : null;
            return CommentResponse.fromComment(comment, userVote);
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable String postId,
            @RequestBody CreateCommentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String userId = extractUserId(authHeader);
            Comment comment = commentService.createComment(postId, request.content(), userId);

            // Devolvemos 201 CREATED con el DTO formateado
            return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.fromComment(comment, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String userId = extractUserId(authHeader);
            commentService.deleteComment(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/comments/{id}/vote")
    public ResponseEntity<?> vote(
            @PathVariable String id,
            @RequestParam String type,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String userId = extractUserId(authHeader);
            CommentService.VoteResult result = commentService.vote(id, type, userId);
            return ResponseEntity.ok(Map.of(
                    "upvotes", result.upvotes(),
                    "downvotes", result.downvotes(),
                    "userVote", result.userVote() != null ? result.userVote() : ""
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- UTILIDADES DE SEGURIDAD ---

    private String extractUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }

    private String getUserIdIfAuthenticated(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authHeader.replace("Bearer ", "");
            if (jwtService.isTokenValid(token)) {
                return jwtService.extractUserId(token);
            }
        } catch (Exception e) {
            // Token inválido, devuelve null silenciosamente
        }
        return null;
    }}