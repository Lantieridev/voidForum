package com.voidforum.service;

import com.voidforum.dto.PostCreateDto;
import com.voidforum.dto.PostResponseDto;
import com.voidforum.exception.ForbiddenException;
import com.voidforum.exception.ResourceNotFoundException;
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

        assertThrows(ResourceNotFoundException.class, () -> {
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

        assertThrows(ForbiddenException.class, () -> {
            postService.deletePost("post123", "otheruser");
        });
    }

    @Test
    void deletePost_NotFound() {
        when(postRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.deletePost("missing", "testuser");
        });
    }

    @Test
    void updatePost_Forbidden() {
        when(postRepository.findById("post123")).thenReturn(Optional.of(post));

        assertThrows(ForbiddenException.class, () -> {
            postService.updatePost("post123", postCreateDto, "otheruser");
        });
    }

    @Test
    void updatePost_NotFound() {
        when(postRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost("missing", postCreateDto, "testuser");
        });
    }

    @Test
    void updatePost_updatesContentAndTags_whenTheCallerIsTheAuthor() {
        when(postRepository.findById("post123")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findById("user123")).thenReturn(Optional.of(author));

        PostCreateDto update = new PostCreateDto();
        update.setContent("Edited content");
        update.setTags(List.of("edited"));

        PostResponseDto result = postService.updatePost("post123", update, "testuser");

        assertEquals("Edited content", result.content());
        assertEquals(List.of("edited"), result.tags());
    }

    @Test
    void getAllPosts_sortsNewestFirst() {
        Post older = Post.builder().id("p1").createdAt(LocalDateTime.now().minusDays(1)).build();
        Post newer = Post.builder().id("p2").createdAt(LocalDateTime.now()).build();
        when(postRepository.findAll()).thenReturn(List.of(older, newer));

        List<PostResponseDto> result = postService.getAllPosts();

        assertEquals("p2", result.get(0).id());
        assertEquals("p1", result.get(1).id());
    }

    @Test
    void getAllPosts_sortsPostsWithNoCreatedAtToTheEnd_insteadOfThrowing() {
        Post noDate = Post.builder().id("p1").createdAt(null).build();
        Post withDate = Post.builder().id("p2").createdAt(LocalDateTime.now()).build();
        when(postRepository.findAll()).thenReturn(List.of(noDate, withDate));

        List<PostResponseDto> result = postService.getAllPosts();

        assertEquals("p2", result.get(0).id());
        assertEquals("p1", result.get(1).id());
    }

    @Test
    void getFeed_sortsNewestFirst() {
        Post older = Post.builder().id("p1").authorId("a1").createdAt(LocalDateTime.now().minusDays(1)).build();
        Post newer = Post.builder().id("p2").authorId("a1").createdAt(LocalDateTime.now()).build();
        when(postRepository.findByAuthorIdIn(List.of("a1"))).thenReturn(List.of(older, newer));

        List<PostResponseDto> result = postService.getFeed(List.of("a1"));

        assertEquals("p2", result.get(0).id());
    }

    @Test
    void searchPosts_delegatesToTheRepositoryAndMapsResults() {
        when(postRepository.searchPosts("java")).thenReturn(List.of(post));

        List<PostResponseDto> result = postService.searchPosts("java");

        assertEquals(1, result.size());
        assertEquals("post123", result.get(0).id());
    }

    @Test
    void searchByTag_delegatesToTheRepositoryAndMapsResults() {
        when(postRepository.searchByTag("java")).thenReturn(List.of(post));

        assertEquals(1, postService.searchByTag("java").size());
    }

    @Test
    void searchByAuthor_delegatesToTheRepositoryAndMapsResults() {
        when(postRepository.searchByAuthor("testuser")).thenReturn(List.of(post));

        assertEquals(1, postService.searchByAuthor("testuser").size());
    }

    @Test
    void searchByContent_delegatesToTheRepositoryAndMapsResults() {
        when(postRepository.searchByContent("hello")).thenReturn(List.of(post));

        assertEquals(1, postService.searchByContent("hello").size());
    }

    @Test
    void getPostsByIds_delegatesToTheRepositoryAndMapsResults() {
        when(postRepository.findByIdIn(List.of("post123"))).thenReturn(List.of(post));

        assertEquals(1, postService.getPostsByIds(List.of("post123")).size());
    }

    @Test
    void incrementSavedCount_addsOneToTheExistingCount() {
        post.setSavedCount(2);
        when(postRepository.findById("post123")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = postService.incrementSavedCount("post123");

        assertEquals(3, result.getSavedCount());
    }

    @Test
    void incrementSavedCount_treatsANullCountAsZero() {
        post.setSavedCount(null);
        when(postRepository.findById("post123")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = postService.incrementSavedCount("post123");

        assertEquals(1, result.getSavedCount());
    }

    @Test
    void decrementSavedCount_subtractsOne() {
        post.setSavedCount(2);
        when(postRepository.findById("post123")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = postService.decrementSavedCount("post123");

        assertEquals(1, result.getSavedCount());
    }

    @Test
    void decrementSavedCount_neverGoesBelowZero() {
        post.setSavedCount(0);
        when(postRepository.findById("post123")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = postService.decrementSavedCount("post123");

        assertEquals(0, result.getSavedCount());
    }

    @Test
    void anonymizeUserPosts_rewritesTheAuthorUsernameOnEveryMatchingPost() {
        Post p1 = Post.builder().id("p1").authorUsername("testuser").build();
        Post p2 = Post.builder().id("p2").authorUsername("testuser").build();
        when(postRepository.findByAuthorUsername("testuser")).thenReturn(List.of(p1, p2));

        postService.anonymizeUserPosts("testuser", "[deleted]-xyz");

        assertEquals("[deleted]-xyz", p1.getAuthorUsername());
        assertEquals("[deleted]-xyz", p2.getAuthorUsername());
        verify(postRepository).saveAll(List.of(p1, p2));
    }

    @Test
    void mapToResponseDto_fillsInSafeDefaults_whenOptionalFieldsAreMissing() {
        Post bareMinimum = Post.builder().id("p1").build(); // no content, tags, authorId, counts, createdAt
        when(postRepository.findAll()).thenReturn(List.of(bareMinimum));

        PostResponseDto result = postService.getAllPosts().get(0);

        assertEquals("", result.content());
        assertEquals("Unknown", result.authorUsername());
        assertEquals("", result.authorId());
        assertEquals(List.of(), result.tags());
        assertEquals(0, result.voteCount());
        assertEquals(0, result.commentCount());
        assertEquals(0, result.savedCount());
        verify(userRepository, never()).findById(any());
    }
}
