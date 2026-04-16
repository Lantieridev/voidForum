package com.voidforum.controller;

import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.model.User;
import com.voidforum.repository.UserRepository;
import com.voidforum.service.CommentService;
import com.voidforum.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable String postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable String postId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        try {
            String username = extractUsername(authHeader);
            CommentCreateDto dto = new CommentCreateDto();
            dto.setContent(request.get("content"));
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
            @RequestHeader(value = "Authorization", required = false) String authHeader
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
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        try {
            String username = extractUsername(authHeader);
            CommentCreateDto dto = new CommentCreateDto();
            dto.setContent(request.get("content"));
            CommentResponseDto updated = commentService.updateComment(id, dto, username);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String extractUsername(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header missing or invalid");
        }
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUsername(token);
    }
}
