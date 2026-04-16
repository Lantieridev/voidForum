package com.voidforum.controller;

import com.voidforum.dto.*;
import com.voidforum.model.User;
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
            return ResponseEntity.ok(new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    user.getBio(),
                    user.getFollowerCount(),
                    user.getFollowingCount(),
                    false,
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto dto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.updateProfile(username, dto);
            return ResponseEntity.ok(new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    user.getBio(),
                    user.getFollowerCount(),
                    user.getFollowingCount(),
                    false,
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.changePassword(username, dto.currentPassword(), dto.newPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me/notifications")
    public ResponseEntity<?> updateNotifications(@RequestBody UpdateNotificationsDto dto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.updateNotifications(username, dto);
            return ResponseEntity.ok(new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    user.getBio(),
                    user.getFollowerCount(),
                    user.getFollowingCount(),
                    false,
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(@RequestBody Map<String, String> body) {
        try {
            String password = body.get("password");
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Contraseña requerida para eliminar la cuenta"));
            }
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.deleteAccount(username, password);
            return ResponseEntity.ok(Map.of("message", "Cuenta eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.findById(id);
            boolean isFollowing = false;
            try {
                isFollowing = userService.isFollowing(currentUsername, id);
            } catch (Exception ignored) {}
            return ResponseEntity.ok(new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    user.getBio(),
                    user.getFollowerCount(),
                    user.getFollowingCount(),
                    isFollowing,
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followUser(@PathVariable String id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.follow(username, id);
            return ResponseEntity.ok(Map.of("message", "Ahora sigues a este usuario"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<?> unfollowUser(@PathVariable String id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.unfollow(username, id);
            return ResponseEntity.ok(Map.of("message", "Has dejado de seguir a este usuario"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable String id) {
        try {
            List<User> followers = userService.getFollowers(id);
            return ResponseEntity.ok(followers.stream()
                    .map(u -> new UserProfileDto(
                            u.getId(),
                            u.getUsername(),
                            u.getDisplayName(),
                            u.getBio(),
                            u.getFollowerCount(),
                            u.getFollowingCount(),
                            false,
                            u.getCreatedAt()
                    ))
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<?> getFollowing(@PathVariable String id) {
        try {
            List<User> following = userService.getFollowing(id);
            return ResponseEntity.ok(following.stream()
                    .map(u -> new UserProfileDto(
                            u.getId(),
                            u.getUsername(),
                            u.getDisplayName(),
                            u.getBio(),
                            u.getFollowerCount(),
                            u.getFollowingCount(),
                            false,
                            u.getCreatedAt()
                    ))
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/isfollowing")
    public ResponseEntity<?> isFollowing(@PathVariable String id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean following = userService.isFollowing(username, id);
            return ResponseEntity.ok(Map.of("isFollowing", following));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me/following")
    public ResponseEntity<?> getMyFollowing() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<String> followingIds = userService.getFollowingIds(username);
            return ResponseEntity.ok(followingIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/saved/{postId}")
    public ResponseEntity<?> savePost(@PathVariable String postId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.findByUsername(username);
            userService.savePost(user.getId(), postId);
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
            userService.unsavePost(user.getId(), postId);
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