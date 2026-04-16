package com.voidforum.controller;

import com.voidforum.dto.PostResponseDto;
import com.voidforum.dto.UserResponseDto;
import com.voidforum.service.PostService;
import com.voidforum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.findByUsername(username);
            return ResponseEntity.ok(new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            var user = userService.findById(id);
            return ResponseEntity.ok(new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/saved/{postId}")
    public ResponseEntity<?> savePost(@PathVariable String postId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.findByUsername(username);
            var updatedUser = userService.savePost(user.getId(), postId);
            postService.incrementSavedCount(postId);
            int savedCount = postService.getPostsByIds(List.of(postId)).get(0).savedCount();
            return ResponseEntity.ok(Map.of("saved", true, "savedCount", savedCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/saved/{postId}")
    public ResponseEntity<?> unsavePost(@PathVariable String postId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.findByUsername(username);
            var updatedUser = userService.unsavePost(user.getId(), postId);
            postService.decrementSavedCount(postId);
            int savedCount = postService.getPostsByIds(List.of(postId)).get(0).savedCount();
            return ResponseEntity.ok(Map.of("saved", false, "savedCount", savedCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/saved")
    public ResponseEntity<?> getSavedPosts() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.findByUsername(username);
            List<String> savedPostIds = userService.getSavedPosts(user.getId());
            List<PostResponseDto> savedPosts = postService.getPostsByIds(savedPostIds);
            return ResponseEntity.ok(Map.of("savedPosts", savedPosts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}