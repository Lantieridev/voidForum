package com.voidforum.controller;

import com.voidforum.domain.model.User;
import com.voidforum.domain.port.in.PostUseCase;
import com.voidforum.domain.port.in.UserUseCase;
import com.voidforum.dto.*;
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

    private final UserUseCase userUseCase;
    private final PostUseCase postUseCase;

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
        var user = userUseCase.findByUsername(currentUsername());
        return ResponseEntity.ok(toProfileDto(user, false));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateProfile(@RequestBody UpdateProfileDto dto) {
        var user = userUseCase.updateProfile(currentUsername(), dto);
        return ResponseEntity.ok(toProfileDto(user, false));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordDto dto) {
        userUseCase.changePassword(currentUsername(), dto.currentPassword(), dto.newPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }

    @PutMapping("/me/notifications")
    public ResponseEntity<UserProfileDto> updateNotifications(@RequestBody UpdateNotificationsDto dto) {
        var user = userUseCase.updateNotifications(currentUsername(), dto);
        return ResponseEntity.ok(toProfileDto(user, false));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteAccount(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contraseña requerida para eliminar la cuenta"));
        }
        userUseCase.deleteAccount(currentUsername(), password);
        return ResponseEntity.ok(Map.of("message", "Cuenta eliminada correctamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable String id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userUseCase.findById(id);
        boolean isFollowing = false;
        try {
            isFollowing = userUseCase.isFollowing(currentUsername, id);
        } catch (Exception ignored) {}
        return ResponseEntity.ok(toProfileDto(user, isFollowing));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Map<String, String>> followUser(@PathVariable String id) {
        userUseCase.follow(currentUsername(), id);
        return ResponseEntity.ok(Map.of("message", "Ahora sigues a este usuario"));
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Map<String, String>> unfollowUser(@PathVariable String id) {
        userUseCase.unfollow(currentUsername(), id);
        return ResponseEntity.ok(Map.of("message", "Has dejado de seguir a este usuario"));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserProfileDto>> getFollowers(@PathVariable String id) {
        List<User> followers = userUseCase.getFollowers(id);
        return ResponseEntity.ok(followers.stream().map(u -> toProfileDto(u, false)).toList());
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<UserProfileDto>> getFollowing(@PathVariable String id) {
        List<User> following = userUseCase.getFollowing(id);
        return ResponseEntity.ok(following.stream().map(u -> toProfileDto(u, false)).toList());
    }

    @GetMapping("/{id}/isfollowing")
    public ResponseEntity<Map<String, Boolean>> isFollowing(@PathVariable String id) {
        boolean following = userUseCase.isFollowing(currentUsername(), id);
        return ResponseEntity.ok(Map.of("isFollowing", following));
    }

    @GetMapping("/me/following")
    public ResponseEntity<List<String>> getMyFollowing() {
        List<String> followingIds = userUseCase.getFollowingIds(currentUsername());
        return ResponseEntity.ok(followingIds);
    }

    @PostMapping("/saved/{postId}")
    public ResponseEntity<Map<String, Object>> savePost(@PathVariable String postId) {
        var user = userUseCase.findByUsername(currentUsername());
        userUseCase.savePost(user.getId(), postId);
        postUseCase.incrementSavedCount(postId);
        int savedCount = postUseCase.getPostsByIds(List.of(postId)).get(0).savedCount();
        return ResponseEntity.ok(Map.of("saved", true, "savedCount", savedCount));
    }

    @DeleteMapping("/saved/{postId}")
    public ResponseEntity<Map<String, Object>> unsavePost(@PathVariable String postId) {
        var user = userUseCase.findByUsername(currentUsername());
        userUseCase.unsavePost(user.getId(), postId);
        postUseCase.decrementSavedCount(postId);
        int savedCount = postUseCase.getPostsByIds(List.of(postId)).get(0).savedCount();
        return ResponseEntity.ok(Map.of("saved", false, "savedCount", savedCount));
    }

    @GetMapping("/saved")
    public ResponseEntity<Map<String, Object>> getSavedPosts() {
        var user = userUseCase.findByUsername(currentUsername());
        List<String> savedPostIds = userUseCase.getSavedPosts(user.getId());
        List<PostResponseDto> savedPosts = postUseCase.getPostsByIds(savedPostIds);
        return ResponseEntity.ok(Map.of("savedPosts", savedPosts));
    }
}
