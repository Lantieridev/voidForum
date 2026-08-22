package com.voidforum.controller;

import com.voidforum.domain.port.in.CommentUseCase;
import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.exception.UnauthorizedException;
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

    private final CommentUseCase commentUseCase;
    private final JwtService jwtService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable String postId) {
        List<CommentResponseDto> comments = commentUseCase.getCommentsByPost(postId, null);
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
        CommentResponseDto comment = commentUseCase.createComment(dto, username);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String username = extractUsername(authHeader);
        commentUseCase.deleteComment(id, username);
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
        CommentResponseDto updated = commentUseCase.updateComment(id, dto, username);
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
            throw new UnauthorizedException("Token inválido o expirado");
        }
    }
}
