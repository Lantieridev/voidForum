package com.voidforum.controller;

import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.service.CommentService;
import com.voidforum.service.JwtService;
import lombok.RequiredArgsConstructor;
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

    public record CreateCommentRequest(String content) {}

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable String postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable String postId,
            @RequestBody CreateCommentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String username = extractUsername(authHeader);
            CommentCreateDto dto = new CommentCreateDto();
            dto.setContent(request.content());
            dto.setPostId(postId);
            CommentResponseDto comment = commentService.createComment(dto, username);
            return ResponseEntity.ok(comment);
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
            String username = extractUsername(authHeader);
            commentService.deleteComment(id, username);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable String id,
            @RequestBody CreateCommentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String username = extractUsername(authHeader);
            CommentCreateDto dto = new CommentCreateDto();
            dto.setContent(request.content());
            CommentResponseDto updated = commentService.updateComment(id, dto, username);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String extractUsername(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUsername(token);
    }
}