package com.voidforum.domain.port.in;

import com.voidforum.domain.model.Post;
import com.voidforum.dto.PostCreateDto;
import com.voidforum.dto.PostResponseDto;

import java.util.List;

public interface PostUseCase {
    PostResponseDto createPost(PostCreateDto request, String username);
    List<PostResponseDto> getAllPosts();
    List<PostResponseDto> searchPosts(String query);
    List<PostResponseDto> searchByTag(String tag);
    List<PostResponseDto> searchByAuthor(String username);
    List<PostResponseDto> searchByContent(String content);
    List<PostResponseDto> getFeed(List<String> followingIds);
    PostResponseDto updatePost(String id, PostCreateDto postRequest, String currentUsername);
    void deletePost(String postId, String username);
    void anonymizeUserPosts(String oldUsername, String newUsername);
    List<PostResponseDto> getPostsByIds(List<String> ids);
    Post incrementSavedCount(String postId);
    Post decrementSavedCount(String postId);
}
