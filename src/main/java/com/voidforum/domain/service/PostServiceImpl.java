package com.voidforum.domain.service;

import com.voidforum.domain.model.Post;
import com.voidforum.domain.model.User;
import com.voidforum.domain.port.in.PostUseCase;
import com.voidforum.domain.port.out.CommentRepositoryPort;
import com.voidforum.domain.port.out.PostRepositoryPort;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.domain.port.out.VoteRepositoryPort;
import com.voidforum.dto.PostCreateDto;
import com.voidforum.dto.PostResponseDto;
import com.voidforum.exception.ForbiddenException;
import com.voidforum.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostUseCase {

    private final PostRepositoryPort postRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final CommentRepositoryPort commentRepositoryPort;
    private final VoteRepositoryPort voteRepositoryPort;

    @Override
    public PostResponseDto createPost(PostCreateDto request, String username) {
        User author = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Post post = Post.builder()
                .content(request.getContent())
                .tags(request.getTags())
                .authorId(author.getId())
                .authorUsername(author.getUsername())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponseDto(postRepositoryPort.save(post));
    }

    @Override
    public List<PostResponseDto> getAllPosts() {
        return postRepositoryPort.findAll().stream()
                .sorted((p1, p2) -> {
                    if (p1.getCreatedAt() == null) return 1;
                    if (p2.getCreatedAt() == null) return -1;
                    return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                })
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDto> searchPosts(String query) {
        return postRepositoryPort.searchPosts(query).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDto> searchByTag(String tag) {
        return postRepositoryPort.searchByTag(tag).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDto> searchByAuthor(String username) {
        return postRepositoryPort.searchByAuthor(username).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDto> searchByContent(String content) {
        return postRepositoryPort.searchByContent(content).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDto> getFeed(List<String> followingIds) {
        return postRepositoryPort.findByAuthorIdIn(followingIds).stream()
                .sorted((p1, p2) -> {
                    if (p1.getCreatedAt() == null) return 1;
                    if (p2.getCreatedAt() == null) return -1;
                    return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                })
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponseDto updatePost(String id, PostCreateDto postRequest, String currentUsername) {
        Post post = postRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));

        if (!post.getAuthorUsername().equals(currentUsername)) {
            throw new ForbiddenException("No tenés permiso para editar este post");
        }

        post.setContent(postRequest.getContent());
        post.setTags(postRequest.getTags());

        Post updatedPost = postRepositoryPort.save(post);
        return mapToResponseDto(updatedPost);
    }

    @Override
    public void deletePost(String postId, String username) {
        Post post = postRepositoryPort.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));

        if (!post.getAuthorUsername().equals(username)) {
            throw new ForbiddenException("No tienes permiso para borrar este post");
        }

        voteRepositoryPort.deleteAllByTargetIdAndTargetType(postId, "post");
        commentRepositoryPort.deleteAllByPostId(postId);
        postRepositoryPort.deleteById(postId);
    }

    @Override
    public void anonymizeUserPosts(String oldUsername, String newUsername) {
        List<Post> posts = postRepositoryPort.findByAuthorUsername(oldUsername);
        for (Post post : posts) {
            post.setAuthorUsername(newUsername);
        }
        postRepositoryPort.saveAll(posts);
    }

    @Override
    public List<PostResponseDto> getPostsByIds(List<String> ids) {
        return postRepositoryPort.findByIdIn(ids).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Post incrementSavedCount(String postId) {
        Post post = postRepositoryPort.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));
        post.setSavedCount((post.getSavedCount() != null ? post.getSavedCount() : 0) + 1);
        return postRepositoryPort.save(post);
    }

    @Override
    public Post decrementSavedCount(String postId) {
        Post post = postRepositoryPort.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));
        if (post.getSavedCount() != null && post.getSavedCount() > 0) {
            post.setSavedCount(post.getSavedCount() - 1);
        }
        return postRepositoryPort.save(post);
    }

    private PostResponseDto mapToResponseDto(Post post) {
        String authorDisplayName = null;
        if (post.getAuthorId() != null) {
            var author = userRepositoryPort.findById(post.getAuthorId()).orElse(null);
            if (author != null) {
                authorDisplayName = author.getDisplayName();
            }
        }

        return new PostResponseDto(
                post.getId(),
                post.getContent() != null ? post.getContent() : "",
                post.getAuthorUsername() != null ? post.getAuthorUsername() : "Unknown",
                post.getAuthorId() != null ? post.getAuthorId() : "",
                post.getTags() != null ? post.getTags() : List.of(),
                post.getVoteCount() != null ? post.getVoteCount() : 0,
                post.getCommentCount() != null ? post.getCommentCount() : 0,
                post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now(),
                post.getSavedCount() != null ? post.getSavedCount() : 0,
                authorDisplayName
        );
    }
}
