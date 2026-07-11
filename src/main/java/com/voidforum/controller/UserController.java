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

    private UserProfileDto toProfileDto(User user, boolean isFollowing) {
        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getBio(),
                user.getFollowerCount(),
                user.getFollowingCount(),
                isFollowing,
                user.getCreatedAt()
        );
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        var user = userService.findByUsername(currentUsername());
        return ResponseEntity.ok(toProfileDto(user, false));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateProfile(@RequestBody UpdateProfileDto dto) {
        var user = userService.updateProfile(currentUsername(), dto);
        return ResponseEntity.ok(toProfileDto(user, false));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordDto dto) {
        userService.changePassword(currentUsername(), dto.currentPassword(), dto.newPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }

    @PutMapping("/me/notifications")
    public ResponseEntity<UserProfileDto> updateNotifications(@RequestBody UpdateNotificationsDto dto) {
        var user = userService.updateNotifications(currentUsername(), dto);
        return ResponseEntity.ok(toProfileDto(user, false));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteAccount(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contraseña requerida para eliminar la cuenta"));
        }
        userService.deleteAccount(currentUsername(), password);
        return ResponseEntity.ok(Map.of("message", "Cuenta eliminada correctamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable String id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findById(id);
        boolean isFollowing = false;
        try {
            isFollowing = userService.isFollowing(currentUsername, id);
        } catch (Exception ignored) {}
        return ResponseEntity.ok(toProfileDto(user, isFollowing));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Map<String, String>> followUser(@PathVariable String id) {
        userService.follow(currentUsername(), id);
        return ResponseEntity.ok(Map.of("message", "Ahora sigues a este usuario"));
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Map<String, String>> unfollowUser(@PathVariable String id) {
        userService.unfollow(currentUsername(), id);
        return ResponseEntity.ok(Map.of("message", "Has dejado de seguir a este usuario"));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserProfileDto>> getFollowers(@PathVariable String id) {
        List<User> followers = userService.getFollowers(id);
        return ResponseEntity.ok(followers.stream().map(u -> toProfileDto(u, false)).toList());
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<UserProfileDto>> getFollowing(@PathVariable String id) {
        List<User> following = userService.getFollowing(id);
        return ResponseEntity.ok(following.stream().map(u -> toProfileDto(u, false)).toList());
    }

    @GetMapping("/{id}/isfollowing")
    public ResponseEntity<Map<String, Boolean>> isFollowing(@PathVariable String id) {
        boolean following = userService.isFollowing(currentUsername(), id);
        return ResponseEntity.ok(Map.of("isFollowing", following));
    }

    @GetMapping("/me/following")
    public ResponseEntity<List<String>> getMyFollowing() {
        List<String> followingIds = userService.getFollowingIds(currentUsername());
        return ResponseEntity.ok(followingIds);
    }

    @PostMapping("/saved/{postId}")
    public ResponseEntity<Map<String, Object>> savePost(@PathVariable String postId) {
        var user = userService.findByUsername(currentUsername());
        userService.savePost(user.getId(), postId);
        postService.incrementSavedCount(postId);
        int savedCount = postService.getPostsByIds(List.of(postId)).get(0).savedCount();
        return ResponseEntity.ok(Map.of("saved", true, "savedCount", savedCount));
    }

    @DeleteMapping("/saved/{postId}")
    public ResponseEntity<Map<String, Object>> unsavePost(@PathVariable String postId) {
        var user = userService.findByUsername(currentUsername());
        userService.unsavePost(user.getId(), postId);
        postService.decrementSavedCount(postId);
        int savedCount = postService.getPostsByIds(List.of(postId)).get(0).savedCount();
        return ResponseEntity.ok(Map.of("saved", false, "savedCount", savedCount));
    }

    @GetMapping("/saved")
    public ResponseEntity<Map<String, Object>> getSavedPosts() {
        var user = userService.findByUsername(currentUsername());
        List<String> savedPostIds = userService.getSavedPosts(user.getId());
        List<PostResponseDto> savedPosts = postService.getPostsByIds(savedPostIds);
        return ResponseEntity.ok(Map.of("savedPosts", savedPosts));
    }
}
