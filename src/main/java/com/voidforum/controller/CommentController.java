package com.voidforum.controller;

import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.exception.UnauthorizedException;
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
        List<CommentResponseDto> comments = commentService.getCommentsByPost(postId, null);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable String postId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String username = extractUsername(authHeader);
        CommentCreateDto dto = new CommentCreateDto();
        dto.setContent(request.get("content"));
        dto.setPostId(postId);
        dto.setParentCommentId(request.get("parentCommentId"));
        CommentResponseDto comment = commentService.createComment(dto, username);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String username = extractUsername(authHeader);
        commentService.deleteComment(id, username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable String id,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String username = extractUsername(authHeader);
        CommentCreateDto dto = new CommentCreateDto();
        dto.setContent(request.get("content"));
        CommentResponseDto updated = commentService.updateComment(id, dto, username);
        return ResponseEntity.ok(updated);
    }

    private String extractUsername(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header missing or invalid");
        }
        String token = authHeader.replace("Bearer ", "");
        try {
            return jwtService.extractUsername(token);
        } catch (RuntimeException e) {
            // JwtService.extractUsername parses the token unguarded (unlike validateToken,
            // which catches parse failures) - a malformed/expired/tampered token throws here.
            throw new UnauthorizedException("Token inválido o expirado");
        }
    }
}
