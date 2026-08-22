package com.voidforum.controller;

import com.voidforum.domain.port.in.PostUseCase;
import com.voidforum.domain.port.in.UserUseCase;
import com.voidforum.dto.PostCreateDto;
import com.voidforum.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostUseCase postUseCase;
    private final UserUseCase userUseCase;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostCreateDto request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(201).body(postUseCase.createPost(request, username));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postUseCase.getAllPosts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostResponseDto>> searchPosts(@RequestParam String q) {
        return ResponseEntity.ok(postUseCase.searchPosts(q));
    }

    @GetMapping("/search/by-tag")
    public ResponseEntity<List<PostResponseDto>> searchByTag(@RequestParam String tag) {
        return ResponseEntity.ok(postUseCase.searchByTag(tag));
    }

    @GetMapping("/search/by-author")
    public ResponseEntity<List<PostResponseDto>> searchByAuthor(@RequestParam String username) {
        return ResponseEntity.ok(postUseCase.searchByAuthor(username));
    }

    @GetMapping("/search/by-content")
    public ResponseEntity<List<PostResponseDto>> searchByContent(@RequestParam String content) {
        return ResponseEntity.ok(postUseCase.searchByContent(content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        postUseCase.deletePost(id, username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable String id,
            @RequestBody PostCreateDto postRequest,
            Principal principal) {
        return ResponseEntity.ok(postUseCase.updatePost(id, postRequest, principal.getName()));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponseDto>> getFeed() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String> followingIds = userUseCase.getFollowingIds(username);
        if (followingIds == null || followingIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(postUseCase.getFeed(followingIds));
    }
}