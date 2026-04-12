package com.voidforum.controller;

import com.voidforum.model.Post;
import com.voidforum.service.JwtService;
import com.voidforum.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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

    // --- DTOs (Records) para mandar y recibir datos de forma segura ---
    public record CreatePostRequest(String title, String content, List<String> tags) {}
    public record UpdatePostRequest(String title, String content, List<String> tags) {}

    // Este record reemplaza a todos los peligrosos Map.of()
    public record PostResponse(
            String id, String title, String content, String authorId,
            String authorUsername, List<String> tags, String createdAt,
            int upvotes, int downvotes, String userVote
    ) {
        // Un "traductor" automático de Post a PostResponse
        public static PostResponse fromPost(Post post, String userVote) {
            return new PostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getAuthorId(),
                    post.getAuthorUsername(),
                    post.getTags() != null ? post.getTags() : List.of(),
                    post.getCreatedAt() != null ? post.getCreatedAt().toString() : "",
                    post.getUpvotes(),
                    post.getDownvotes(),
                    userVote != null ? userVote : ""
            );
        }
    }

    // --- ENDPOINTS ---

    @GetMapping
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Page<Post> posts;
        if (tag != null && !tag.isEmpty()) {
            posts = postService.getPostsByTag(tag, PageRequest.of(page, size));
        } else {
            posts = postService.getAllPosts(PageRequest.of(page, size));
        }

        // Usamos nuestro record seguro en vez de Map.of
        List<PostResponse> content = posts.getContent().stream()
                .map(post -> PostResponse.fromPost(post, null))
                .toList();

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

            return ResponseEntity.ok(PostResponse.fromPost(post, userVote));
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
            return ResponseEntity.status(HttpStatus.CREATED).body(PostResponse.fromPost(post, null));
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
            return ResponseEntity.ok(PostResponse.fromPost(post, null));
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
            @RequestParam String type, // "up" o "down"
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
    }
}