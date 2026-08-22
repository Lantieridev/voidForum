package com.voidforum.service;

import com.voidforum.domain.model.Comment;
import com.voidforum.domain.model.Post;
import com.voidforum.domain.model.User;
import com.voidforum.domain.model.Vote;
import com.voidforum.domain.port.out.CommentRepositoryPort;
import com.voidforum.domain.port.out.PostRepositoryPort;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.domain.port.out.VoteRepositoryPort;
import com.voidforum.domain.service.CommentServiceImpl;
import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.exception.ForbiddenException;
import com.voidforum.exception.ResourceNotFoundException;
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

    private final CommentRepositoryPort commentRepositoryPort = mock(CommentRepositoryPort.class);
    private final PostRepositoryPort postRepositoryPort = mock(PostRepositoryPort.class);
    private final UserRepositoryPort userRepositoryPort = mock(UserRepositoryPort.class);
    private final VoteRepositoryPort voteRepositoryPort = mock(VoteRepositoryPort.class);
    private final CommentServiceImpl commentService =
            new CommentServiceImpl(commentRepositoryPort, postRepositoryPort, userRepositoryPort, voteRepositoryPort);

    // ── createComment ───────────────────────────────────────────────────

    @Test
    void createComment_savesARootCommentAndIncrementsThePostsCommentCount() {
        CommentCreateDto dto = new CommentCreateDto("Buen post!", "post-1", null);
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.of(Post.builder().id("post-1").commentCount(2).build()));
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(User.builder().id("u1").username("martin").build()));
        when(commentRepositoryPort.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId("c1");
            return c;
        });
        Post post = Post.builder().id("post-1").commentCount(2).build();
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.of(post));
        when(commentRepositoryPort.findByParentCommentId("c1")).thenReturn(List.of());
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("c1", "comment")).thenReturn(List.of());

        CommentResponseDto result = commentService.createComment(dto, "martin");

        assertThat(result.content()).isEqualTo("Buen post!");
        assertThat(result.authorUsername()).isEqualTo("martin");
        verify(postRepositoryPort).save(argThat(p -> p.getCommentCount() == 3));
    }

    @Test
    void createComment_doesNotTouchThePostsCommentCount_forAReply() {
        CommentCreateDto dto = new CommentCreateDto("Reply", "post-1", "parent-1");
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.of(Post.builder().id("post-1").build()));
        when(commentRepositoryPort.findById("parent-1")).thenReturn(Optional.of(Comment.builder().id("parent-1").build()));
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(User.builder().id("u1").username("martin").build()));
        when(commentRepositoryPort.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId("c2");
            return c;
        });
        when(commentRepositoryPort.findByParentCommentId("c2")).thenReturn(List.of());
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("c2", "comment")).thenReturn(List.of());

        commentService.createComment(dto, "martin");

        verify(postRepositoryPort, never()).save(any());
    }

    @Test
    void createComment_throwsResourceNotFound_whenThePostDoesNotExist() {
        when(postRepositoryPort.findById("ghost-post")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(new CommentCreateDto("x", "ghost-post", null), "martin"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(commentRepositoryPort, never()).save(any());
    }

    @Test
    void createComment_throwsResourceNotFound_whenTheParentCommentDoesNotExist() {
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.of(Post.builder().id("post-1").build()));
        when(commentRepositoryPort.findById("ghost-parent")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                commentService.createComment(new CommentCreateDto("x", "post-1", "ghost-parent"), "martin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createComment_throwsResourceNotFound_whenTheAuthorDoesNotExist() {
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.of(Post.builder().id("post-1").build()));
        when(userRepositoryPort.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(new CommentCreateDto("x", "post-1", null), "ghost"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getCommentsByPost ───────────────────────────────────────────────

    @Test
    void getCommentsByPost_returnsOnlyRootCommentsNewestFirst_withRepliesNested() {
        Comment older = Comment.builder().id("c1").postId("post-1").createdAt(LocalDateTime.now().minusHours(1)).build();
        Comment newer = Comment.builder().id("c2").postId("post-1").createdAt(LocalDateTime.now()).build();
        Comment reply = Comment.builder().id("c3").postId("post-1").parentCommentId("c1").createdAt(LocalDateTime.now()).build();
        when(commentRepositoryPort.findByPostId("post-1")).thenReturn(List.of(older, newer, reply));
        when(commentRepositoryPort.findByParentCommentId("c1")).thenReturn(List.of(reply));
        when(commentRepositoryPort.findByParentCommentId("c2")).thenReturn(List.of());
        when(commentRepositoryPort.findByParentCommentId("c3")).thenReturn(List.of());
        when(voteRepositoryPort.findAllByTargetIdAndTargetType(any(), eq("comment"))).thenReturn(List.of());

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
        when(commentRepositoryPort.findById("c1")).thenReturn(Optional.of(comment));
        Post post = Post.builder().id("post-1").commentCount(3).build();
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.of(post));

        commentService.deleteComment("c1", "martin");

        verify(commentRepositoryPort).deleteAllByParentCommentId("c1");
        verify(commentRepositoryPort).deleteById("c1");
        verify(postRepositoryPort).save(argThat(p -> p.getCommentCount() == 2));
    }

    @Test
    void deleteComment_doesNotDecrementPostCommentCount_whenDeletingAReply() {
        Comment reply = Comment.builder().id("c2").postId("post-1").parentCommentId("c1").authorUsername("martin").build();
        when(commentRepositoryPort.findById("c2")).thenReturn(Optional.of(reply));

        commentService.deleteComment("c2", "martin");

        verify(postRepositoryPort, never()).save(any());
    }

    @Test
    void deleteComment_neverGoesBelowZero_evenIfTheCountWasAlreadyInconsistent() {
        Comment comment = Comment.builder().id("c1").postId("post-1").authorUsername("martin").build();
        when(commentRepositoryPort.findById("c1")).thenReturn(Optional.of(comment));
        Post post = Post.builder().id("post-1").commentCount(0).build();
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.of(post));

        commentService.deleteComment("c1", "martin");

        verify(postRepositoryPort).save(argThat(p -> p.getCommentCount() == 0));
    }

    @Test
    void deleteComment_throwsForbidden_whenNotTheAuthor() {
        Comment comment = Comment.builder().id("c1").postId("post-1").authorUsername("other-user").build();
        when(commentRepositoryPort.findById("c1")).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment("c1", "martin"))
                .isInstanceOf(ForbiddenException.class);
        verify(commentRepositoryPort, never()).deleteById(any());
    }

    @Test
    void deleteComment_throwsResourceNotFound_whenTheCommentDoesNotExist() {
        when(commentRepositoryPort.findById("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment("ghost", "martin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateComment ───────────────────────────────────────────────────

    @Test
    void updateComment_updatesTheContent_whenTheCallerIsTheAuthor() {
        Comment comment = Comment.builder().id("c1").content("old").authorUsername("martin").build();
        when(commentRepositoryPort.findById("c1")).thenReturn(Optional.of(comment));
        when(commentRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(User.builder().id("u1").build()));
        when(commentRepositoryPort.findByParentCommentId("c1")).thenReturn(List.of());
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("c1", "comment")).thenReturn(List.of());

        CommentResponseDto result = commentService.updateComment("c1", new CommentCreateDto("new content", null, null), "martin");

        assertThat(result.content()).isEqualTo("new content");
    }

    @Test
    void updateComment_throwsForbidden_whenNotTheAuthor() {
        Comment comment = Comment.builder().id("c1").content("old").authorUsername("other-user").build();
        when(commentRepositoryPort.findById("c1")).thenReturn(Optional.of(comment));

        assertThatThrownBy(() ->
                commentService.updateComment("c1", new CommentCreateDto("new", null, null), "martin"))
                .isInstanceOf(ForbiddenException.class);
        verify(commentRepositoryPort, never()).save(any());
    }

    @Test
    void updateComment_throwsResourceNotFound_whenTheCommentDoesNotExist() {
        when(commentRepositoryPort.findById("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.updateComment("ghost", new CommentCreateDto("x", null, null), "martin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── anonymizeUserComments ───────────────────────────────────────────

    @Test
    void anonymizeUserComments_rewritesTheAuthorUsernameOnEveryMatchingComment() {
        Comment c1 = Comment.builder().id("c1").authorUsername("martin").build();
        Comment c2 = Comment.builder().id("c2").authorUsername("martin").build();
        when(commentRepositoryPort.findByAuthorUsername("martin")).thenReturn(List.of(c1, c2));

        commentService.anonymizeUserComments("martin", "[deleted]-abc123");

        assertThat(c1.getAuthorUsername()).isEqualTo("[deleted]-abc123");
        assertThat(c2.getAuthorUsername()).isEqualTo("[deleted]-abc123");
        verify(commentRepositoryPort).saveAll(List.of(c1, c2));
    }

    // ── mapToResponseDto vote fields (exercised via getCommentsByPost) ──

    @Test
    void mapToResponseDto_reportsTheCallingUsersOwnVote() {
        Comment comment = Comment.builder().id("c1").postId("post-1").createdAt(LocalDateTime.now()).build();
        when(commentRepositoryPort.findByPostId("post-1")).thenReturn(List.of(comment));
        when(commentRepositoryPort.findByParentCommentId("c1")).thenReturn(List.of());
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("c1", "comment")).thenReturn(List.of(
                Vote.builder().userId("u1").value(1).build()
        ));
        when(voteRepositoryPort.findByUserIdAndTargetIdAndTargetType("u1", "c1", "comment"))
                .thenReturn(Optional.of(Vote.builder().value(1).build()));

        List<CommentResponseDto> result = commentService.getCommentsByPost("post-1", "u1");

        assertThat(result.get(0).voteCount()).isEqualTo(1);
        assertThat(result.get(0).userVote()).isEqualTo(1);
    }

    @Test
    void mapToResponseDto_reportsZeroUserVote_whenNoUserIdIsProvided() {
        Comment comment = Comment.builder().id("c1").postId("post-1").createdAt(LocalDateTime.now()).build();
        when(commentRepositoryPort.findByPostId("post-1")).thenReturn(List.of(comment));
        when(commentRepositoryPort.findByParentCommentId("c1")).thenReturn(List.of());
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("c1", "comment")).thenReturn(List.of());

        List<CommentResponseDto> result = commentService.getCommentsByPost("post-1", null);

        assertThat(result.get(0).userVote()).isZero();
        verify(voteRepositoryPort, never()).findByUserIdAndTargetIdAndTargetType(any(), any(), any());
    }
}
