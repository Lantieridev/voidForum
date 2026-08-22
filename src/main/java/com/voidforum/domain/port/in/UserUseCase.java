package com.voidforum.domain.port.in;

import com.voidforum.domain.model.User;
import com.voidforum.dto.UpdateNotificationsDto;
import com.voidforum.dto.UpdateProfileDto;

import java.util.List;

public interface UserUseCase {
    User registerUser(User user);
    List<User> getAllUsers();
    User findByUsername(String username);
    User findById(String id);
    User updateProfile(String username, UpdateProfileDto dto);
    User changePassword(String username, String currentPassword, String newPassword);
    User updateNotifications(String username, UpdateNotificationsDto dto);
    void deleteAccount(String username, String password);
    void follow(String currentUsername, String targetUserId);
    void unfollow(String currentUsername, String targetUserId);
    boolean isFollowing(String currentUsername, String targetUserId);
    List<User> getFollowers(String userId);
    List<User> getFollowing(String userId);
    List<String> getFollowingIds(String username);
    User savePost(String userId, String postId);
    User unsavePost(String userId, String postId);
    List<String> getSavedPosts(String userId);
}
