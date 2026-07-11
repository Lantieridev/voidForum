package com.voidforum.service;

import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.exception.ForbiddenException;
import com.voidforum.exception.ResourceNotFoundException;
import com.voidforum.model.Comment;
import com.voidforum.model.Post;
import com.voidforum.model.User;
import com.voidforum.model.Vote;
import com.voidforum.repository.CommentRepository;
import com.voidforum.repository.PostRepository;
import com.voidforum.repository.UserRepository;
import com.voidforum.repository.VoteRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final VoteRepository voteRepository = mock(VoteRepository.class);
    private final CommentService commentService =
            new CommentService(commentRepository, postRepository, userRepository, voteRepository);

    // ── createComment ───────────────────────────────────────────────────

    @Test
    void createComment_savesARootCommentAndIncrementsThePostsCommentCount() {
        CommentCreateDto dto = new CommentCreateDto("Buen post!", "post-1", null);
        when(postRepository.existsById("post-1")).thenReturn(true);
        when(userRepository.findByUsername("martin")).thenReturn(Optional.of(User.builder().id("u1").username("martin").build()));
        when(commentRepository.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId("c1");
            return c;
        });
        Post post = Post.builder().id("post-1").commentCount(2).build();
        when(postRepository.findById("post-1")).thenReturn(Optional.of(post));
        when(commentRepository.findByParentCommentId("c1")).thenReturn(List.of());
        when(voteRepository.findAllByTargetIdAndTargetType("c1", "comment")).thenReturn(List.of());

        CommentResponseDto result = commentService.createComment(dto, "martin");

        assertThat(result.content()).isEqualTo("Buen post!");
        assertThat(result.authorUsername()).isEqualTo("martin");
        verify(postRepository).save(argThat(p -> p.getCommentCount() == 3));
    }

    @Test
    void createComment_doesNotTouchThePostsCommentCount_forAReply() {
        CommentCreateDto dto = new CommentCreateDto("Reply", "post-1", "parent-1");
        when(postRepository.existsById("post-1")).thenReturn(true);
        when(commentRepository.findById("parent-1")).thenReturn(Optional.of(Comment.builder().id("parent-1").build()));
        when(userRepository.findByUsername("martin")).thenReturn(Optional.of(User.builder().id("u1").username("martin").build()));
        when(commentRepository.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId("c2");
            return c;
        });
        when(commentRepository.findByParentCommentId("c2")).thenReturn(List.of());
        when(voteRepository.findAllByTargetIdAndTargetType("c2", "comment")).thenReturn(List.of());

        commentService.createComment(dto, "martin");

        verify(postRepository, never()).save(any());
    }

    @Test
    void createComment_throwsResourceNotFound_whenThePostDoesNotExist() {
        when(postRepository.existsById("ghost-post")).thenReturn(false);

        assertThatThrownBy(() -> commentService.createComment(new CommentCreateDto("x", "ghost-post", null), "martin"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_throwsResourceNotFound_whenTheParentCommentDoesNotExist() {
        when(postRepository.existsById("post-1")).thenReturn(true);
        when(commentRepository.findById("ghost-parent")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                commentService.createComment(new CommentCreateDto("x", "post-1", "ghost-parent"), "martin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createComment_throwsResourceNotFound_whenTheAuthorDoesNotExist() {
        when(postRepository.existsById("post-1")).thenReturn(true);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(new CommentCreateDto("x", "post-1", null), "ghost"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getCommentsByPost ───────────────────────────────────────────────

    @Test
    void getCommentsByPost_returnsOnlyRootCommentsNewestFirst_withRepliesNested() {
        Comment older = Comment.builder().id("c1").postId("post-1").createdAt(LocalDateTime.now().minusHours(1)).build();
        Comment newer = Comment.builder().id("c2").postId("post-1").createdAt(LocalDateTime.now()).build();
        Comment reply = Comment.builder().id("c3").postId("post-1").parentCommentId("c1").createdAt(LocalDateTime.now()).build();
        when(commentRepository.findByPostId("post-1")).thenReturn(List.of(older, newer, reply));
        when(commentRepository.findByParentCommentId("c1")).thenReturn(List.of(reply));
        when(commentRepository.findByParentCommentId("c2")).thenReturn(List.of());
        when(commentRepository.findByParentCommentId("c3")).thenReturn(List.of());
        when(voteRepository.findAllByTargetIdAndTargetType(any(), eq("comment"))).thenReturn(List.of());

        List<CommentResponseDto> result = commentService.getCommentsByPost("post-1", null);

        assertThat(result).hasSize(2); // only roots at the top level
        assertThat(result.get(0).id()).isEqualTo("c2"); // newest first
        assertThat(result.get(1).replies()).hasSize(1);
        assertThat(result.get(1).replies().get(0).id()).isEqualTo("c3");
    }

    // ── deleteComment ───────────────────────────────────────────────────

    @Test
    void deleteComment_deletesTheCommentAndItsReplies_andDecrementsPostCommentCount() {
        Comment comment = Comment.builder().id("c1").postId("post-1").authorUsername("martin").build();
        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));
        Post post = Post.builder().id("post-1").commentCount(3).build();
        when(postRepository.findById("post-1")).thenReturn(Optional.of(post));

        commentService.deleteComment("c1", "martin");

        verify(commentRepository).deleteAllByParentCommentId("c1");
        verify(commentRepository).deleteById("c1");
        verify(postRepository).save(argThat(p -> p.getCommentCount() == 2));
    }

    @Test
    void deleteComment_doesNotDecrementPostCommentCount_whenDeletingAReply() {
        Comment reply = Comment.builder().id("c2").postId("post-1").parentCommentId("c1").authorUsername("martin").build();
        when(commentRepository.findById("c2")).thenReturn(Optional.of(reply));

        commentService.deleteComment("c2", "martin");

        verify(postRepository, never()).save(any());
    }

    @Test
    void deleteComment_neverGoesBelowZero_evenIfTheCountWasAlreadyInconsistent() {
        Comment comment = Comment.builder().id("c1").postId("post-1").authorUsername("martin").build();
        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));
        Post post = Post.builder().id("post-1").commentCount(0).build();
        when(postRepository.findById("post-1")).thenReturn(Optional.of(post));

        commentService.deleteComment("c1", "martin");

        verify(postRepository).save(argThat(p -> p.getCommentCount() == 0));
    }

    @Test
    void deleteComment_throwsForbidden_whenNotTheAuthor() {
        Comment comment = Comment.builder().id("c1").postId("post-1").authorUsername("other-user").build();
        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment("c1", "martin"))
                .isInstanceOf(ForbiddenException.class);
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    void deleteComment_throwsResourceNotFound_whenTheCommentDoesNotExist() {
        when(commentRepository.findById("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment("ghost", "martin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateComment ───────────────────────────────────────────────────

    @Test
    void updateComment_updatesTheContent_whenTheCallerIsTheAuthor() {
        Comment comment = Comment.builder().id("c1").content("old").authorUsername("martin").build();
        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findByUsername("martin")).thenReturn(Optional.of(User.builder().id("u1").build()));
        when(commentRepository.findByParentCommentId("c1")).thenReturn(List.of());
        when(voteRepository.findAllByTargetIdAndTargetType("c1", "comment")).thenReturn(List.of());

        CommentResponseDto result = commentService.updateComment("c1", new CommentCreateDto("new content", null, null), "martin");

        assertThat(result.content()).isEqualTo("new content");
    }

    @Test
    void updateComment_throwsForbidden_whenNotTheAuthor() {
        Comment comment = Comment.builder().id("c1").content("old").authorUsername("other-user").build();
        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));

        assertThatThrownBy(() ->
                commentService.updateComment("c1", new CommentCreateDto("new", null, null), "martin"))
                .isInstanceOf(ForbiddenException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_throwsResourceNotFound_whenTheCommentDoesNotExist() {
        when(commentRepository.findById("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.updateComment("ghost", new CommentCreateDto("x", null, null), "martin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── anonymizeUserComments ───────────────────────────────────────────

    @Test
    void anonymizeUserComments_rewritesTheAuthorUsernameOnEveryMatchingComment() {
        Comment c1 = Comment.builder().id("c1").authorUsername("martin").build();
        Comment c2 = Comment.builder().id("c2").authorUsername("martin").build();
        when(commentRepository.findByAuthorUsername("martin")).thenReturn(List.of(c1, c2));

        commentService.anonymizeUserComments("martin", "[deleted]-abc123");

        assertThat(c1.getAuthorUsername()).isEqualTo("[deleted]-abc123");
        assertThat(c2.getAuthorUsername()).isEqualTo("[deleted]-abc123");
        verify(commentRepository).saveAll(List.of(c1, c2));
    }

    // ── mapToResponseDto vote fields (exercised via getCommentsByPost) ──

    @Test
    void mapToResponseDto_reportsTheCallingUsersOwnVote() {
        Comment comment = Comment.builder().id("c1").postId("post-1").createdAt(LocalDateTime.now()).build();
        when(commentRepository.findByPostId("post-1")).thenReturn(List.of(comment));
        when(commentRepository.findByParentCommentId("c1")).thenReturn(List.of());
        when(voteRepository.findAllByTargetIdAndTargetType("c1", "comment")).thenReturn(List.of(
                Vote.builder().userId("u1").value(1).build()
        ));
        when(voteRepository.findByUserIdAndTargetIdAndTargetType("u1", "c1", "comment"))
                .thenReturn(Optional.of(Vote.builder().value(1).build()));

        List<CommentResponseDto> result = commentService.getCommentsByPost("post-1", "u1");

        assertThat(result.get(0).voteCount()).isEqualTo(1);
        assertThat(result.get(0).userVote()).isEqualTo(1);
    }

    @Test
    void mapToResponseDto_reportsZeroUserVote_whenNoUserIdIsProvided() {
        Comment comment = Comment.builder().id("c1").postId("post-1").createdAt(LocalDateTime.now()).build();
        when(commentRepository.findByPostId("post-1")).thenReturn(List.of(comment));
        when(commentRepository.findByParentCommentId("c1")).thenReturn(List.of());
        when(voteRepository.findAllByTargetIdAndTargetType("c1", "comment")).thenReturn(List.of());

        List<CommentResponseDto> result = commentService.getCommentsByPost("post-1", null);

        assertThat(result.get(0).userVote()).isZero();
        verify(voteRepository, never()).findByUserIdAndTargetIdAndTargetType(any(), any(), any());
    }
}
