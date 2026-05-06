package com.voidforum.service;

import com.voidforum.dto.PostCreateDto;
import com.voidforum.dto.PostResponseDto;
import com.voidforum.model.Post;
import com.voidforum.model.User;
import com.voidforum.repository.CommentRepository;
import com.voidforum.repository.PostRepository;
import com.voidforum.repository.UserRepository;
import com.voidforum.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private PostService postService;

    private User author;
    private Post post;
    private PostCreateDto postCreateDto;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id("user123")
                .username("testuser")
                .displayName("Test User")
                .build();

        post = Post.builder()
                .id("post123")
                .content("Test Content")
                .authorId("user123")
                .authorUsername("testuser")
                .createdAt(LocalDateTime.now())
                .tags(List.of("test"))
                .build();

        postCreateDto = new PostCreateDto();
        postCreateDto.setContent("Test Content");
        postCreateDto.setTags(List.of("test"));
    }

    @Test
    void createPost_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(author));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(userRepository.findById("user123")).thenReturn(Optional.of(author));

        PostResponseDto result = postService.createPost(postCreateDto, "testuser");

        assertNotNull(result);
        assertEquals("Test Content", result.content());
        assertEquals("testuser", result.authorUsername());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            postService.createPost(postCreateDto, "unknown");
        });
    }

    @Test
    void deletePost_Success() {
        when(postRepository.findById("post123")).thenReturn(Optional.of(post));

        postService.deletePost("post123", "testuser");

        verify(voteRepository, times(1)).deleteAllByTargetIdAndTargetType("post123", "post");
        verify(commentRepository, times(1)).deleteAllByPostId("post123");
        verify(postRepository, times(1)).deleteById("post123");
    }

    @Test
    void deletePost_Forbidden() {
        when(postRepository.findById("post123")).thenReturn(Optional.of(post));

        assertThrows(RuntimeException.class, () -> {
            postService.deletePost("post123", "otheruser");
        });
    }
}
