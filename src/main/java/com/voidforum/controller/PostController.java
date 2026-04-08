package com.voidforum.controller;

import com.voidforum.model.Post;
import com.voidforum.service.JwtService;
import com.voidforum.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;

    public record CreatePostRequest(String title, String content, List<String> tags) {}
    public record UpdatePostRequest(String title, String content, List<String> tags) {}

    @GetMapping
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String userId = getUserIdIfAuthenticated(authHeader);
        
        Page<Post> posts;
        if (tag != null && !tag.isEmpty()) {
            posts = postService.getPostsByTag(tag, PageRequest.of(page, size));
        } else {
            posts = postService.getAllPosts(PageRequest.of(page, size));
        }

        List<Map<String, Object>> content = posts.getContent().stream().map(post -> {
            Map<String, Object> map = Map.of(
                    "id", post.getId(),
                    "title", post.getTitle(),
                    "content", post.getContent(),
                    "authorId", post.getAuthorId(),
                    "authorUsername", post.getAuthorUsername(),
                    "tags", post.getTags() != null ? post.getTags() : List.of(),
                    "createdAt", post.getCreatedAt().toString(),
                    "upvotes", post.getUpvotes(),
                    "downvotes", post.getDownvotes()
            );
            return map;
        }).toList();

        return ResponseEntity.ok(Map.of(
                "content", content,
                "totalPages", posts.getTotalPages(),
                "totalElements", posts.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String userId = getUserIdIfAuthenticated(authHeader);
        
        try {
            Post post = postService.getPostById(id);
            String userVote = userId != null ? postService.getUserVote(id, userId) : null;

            return ResponseEntity.ok(Map.of(
                    "id", post.getId(),
                    "title", post.getTitle(),
                    "content", post.getContent(),
                    "authorId", post.getAuthorId(),
                    "authorUsername", post.getAuthorUsername(),
                    "tags", post.getTags() != null ? post.getTags() : List.of(),
                    "createdAt", post.getCreatedAt().toString(),
                    "upvotes", post.getUpvotes(),
                    "downvotes", post.getDownvotes(),
                    "userVote", userVote != null ? userVote : ""
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody CreatePostRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String userId = extractUserId(authHeader);
            Post post = postService.createPost(request.title(), request.content(), request.tags(), userId);
            return ResponseEntity.ok(Map.of(
                    "id", post.getId(),
                    "title", post.getTitle(),
                    "content", post.getContent(),
                    "authorId", post.getAuthorId(),
                    "authorUsername", post.getAuthorUsername(),
                    "tags", post.getTags(),
                    "createdAt", post.getCreatedAt().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable String id,
            @RequestBody UpdatePostRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String userId = extractUserId(authHeader);
            Post post = postService.updatePost(id, request.title(), request.content(), request.tags(), userId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String userId = extractUserId(authHeader);
            postService.deletePost(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> vote(
            @PathVariable String id,
            @RequestParam String type,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String userId = extractUserId(authHeader);
            PostService.VoteResult result = postService.vote(id, type, userId);
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
            // Invalid token, return null
        }
        return null;
    }
}