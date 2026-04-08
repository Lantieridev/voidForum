package com.voidforum.controller;

import com.voidforum.model.Comment;
import com.voidforum.service.CommentService;
import com.voidforum.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;

    public record CreateCommentRequest(String content) {}

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable String postId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String userId = getUserIdIfAuthenticated(authHeader);
        
        List<Comment> comments = commentService.getCommentsByPost(postId);
        
        List<Map<String, Object>> response = new ArrayList<>();
        for (Comment comment : comments) {
            String userVote = userId != null ? commentService.getUserVote(comment.getId(), userId) : null;
            Map<String, Object> map = new HashMap<>();
            map.put("id", comment.getId());
            map.put("postId", comment.getPostId());
            map.put("authorId", comment.getAuthorId());
            map.put("authorUsername", comment.getAuthorUsername());
            map.put("content", comment.getContent());
            map.put("createdAt", comment.getCreatedAt().toString());
            map.put("upvotes", comment.getUpvotes());
            map.put("downvotes", comment.getDownvotes());
            map.put("userVote", userVote != null ? userVote : "");
            response.add(map);
        }

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
            return ResponseEntity.ok(Map.of(
                    "id", comment.getId(),
                    "postId", comment.getPostId(),
                    "authorId", comment.getAuthorId(),
                    "authorUsername", comment.getAuthorUsername(),
                    "content", comment.getContent(),
                    "createdAt", comment.getCreatedAt().toString()
            ));
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

    private String extractUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }

    private String getUserIdIfAuthenticated(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }
        try {
            String token = authHeader.replace("Bearer ", "");
            if (jwtService.isTokenValid(token)) {
                return jwtService.extractUserId(token);
            }
        } catch (Exception e) {
            // Invalid token
        }
        return null;
    }
}