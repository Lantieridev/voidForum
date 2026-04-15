package com.voidforum.controller;

import com.voidforum.dto.PostCreateDto;
import com.voidforum.dto.PostResponseDto;
import com.voidforum.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import com.voidforum.dto.PostCreateDto;
import com.voidforum.dto.PostResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostCreateDto request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(201).body(postService.createPost(request, username));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        postService.deletePost(id, username);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable String id,
            @RequestBody PostCreateDto postRequest,
            Principal principal) {
        // principal.getName() nos da el username del token JWT
        return ResponseEntity.ok(postService.updatePost(id, postRequest, principal.getName()));
    }
}