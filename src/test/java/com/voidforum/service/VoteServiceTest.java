package com.voidforum.service;

import com.voidforum.domain.model.Post;
import com.voidforum.domain.model.User;
import com.voidforum.domain.model.Vote;
import com.voidforum.domain.port.out.PostRepositoryPort;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.domain.port.out.VoteRepositoryPort;
import com.voidforum.domain.service.VoteServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VoteServiceTest {

    private final VoteRepositoryPort voteRepositoryPort = mock(VoteRepositoryPort.class);
    private final UserRepositoryPort userRepositoryPort = mock(UserRepositoryPort.class);
    private final PostRepositoryPort postRepositoryPort = mock(PostRepositoryPort.class);
    private final VoteServiceImpl voteService = new VoteServiceImpl(voteRepositoryPort, userRepositoryPort, postRepositoryPort);

    // ── toggleVote ──────────────────────────────────────────────────────

    @Test
    void toggleVote_createsANewVote_whenNoneExistsYet() {
        when(voteRepositoryPort.findByUserIdAndTargetIdAndTargetType("u1", "post-1", "post"))
                .thenReturn(Optional.empty());
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("post-1", "post")).thenReturn(List.of());
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.empty());

        Map<String, Object> result = voteService.toggleVote("post-1", "u1", 1, "post");

        verify(voteRepositoryPort).save(argThat(v ->
                v.getUserId().equals("u1") && v.getTargetId().equals("post-1") && v.getValue() == 1));
        assertThat(result.get("userVote")).isEqualTo(1);
    }

    @Test
    void toggleVote_removesTheVote_whenClickedAgainWithTheSameValue() {
        Vote existing = Vote.builder().id("v1").userId("u1").targetId("post-1").targetType("post").value(1).build();
        when(voteRepositoryPort.findByUserIdAndTargetIdAndTargetType("u1", "post-1", "post"))
                .thenReturn(Optional.of(existing));
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("post-1", "post")).thenReturn(List.of());
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.empty());

        Map<String, Object> result = voteService.toggleVote("post-1", "u1", 1, "post");

        verify(voteRepositoryPort).delete(existing);
        verify(voteRepositoryPort, never()).save(any());
        assertThat(result.get("userVote")).isEqualTo(0);
    }

    @Test
    void toggleVote_switchesTheValue_whenVotingDifferentlyThanBefore() {
        Vote existing = Vote.builder().id("v1").userId("u1").targetId("post-1").targetType("post").value(1).build();
        when(voteRepositoryPort.findByUserIdAndTargetIdAndTargetType("u1", "post-1", "post"))
                .thenReturn(Optional.of(existing));
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("post-1", "post")).thenReturn(List.of());
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.empty());

        Map<String, Object> result = voteService.toggleVote("post-1", "u1", -1, "post");

        verify(voteRepositoryPort).save(existing);
        assertThat(existing.getValue()).isEqualTo(-1);
        assertThat(result.get("userVote")).isEqualTo(-1);
    }

    @Test
    void toggleVote_updatesThePostsDenormalizedVoteCount_forPostTargets() {
        when(voteRepositoryPort.findByUserIdAndTargetIdAndTargetType("u1", "post-1", "post"))
                .thenReturn(Optional.empty());
        Post post = Post.builder().id("post-1").build();
        when(postRepositoryPort.findById("post-1")).thenReturn(Optional.of(post));
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("post-1", "post")).thenReturn(List.of(
                Vote.builder().value(1).build(),
                Vote.builder().value(1).build(),
                Vote.builder().value(-1).build()
        ));

        voteService.toggleVote("post-1", "u1", 1, "post");

        verify(postRepositoryPort).save(argThat(p -> p.getVoteCount() == 2));
    }

    @Test
    void toggleVote_doesNotTouchThePost_forCommentTargets() {
        when(voteRepositoryPort.findByUserIdAndTargetIdAndTargetType("u1", "comment-1", "comment"))
                .thenReturn(Optional.empty());
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("comment-1", "comment")).thenReturn(List.of());

        voteService.toggleVote("comment-1", "u1", 1, "comment");

        verify(postRepositoryPort, never()).findById(any());
        verify(postRepositoryPort, never()).save(any());
    }

    // ── getUserVotedPosts ───────────────────────────────────────────────

    @Test
    void getUserVotedPosts_returnsLikedPostsCreatedPostsAndSavedPosts() {
        when(voteRepositoryPort.findAllByUserIdAndTargetType("u1", "post")).thenReturn(List.of(
                Vote.builder().targetId("liked-1").value(1).build(),
                Vote.builder().targetId("disliked-1").value(-1).build()
        ));
        Post likedPost = Post.builder().id("liked-1").content("c").authorId("author-1").build();
        when(postRepositoryPort.findAllById(List.of("liked-1"))).thenReturn(List.of(likedPost));
        when(postRepositoryPort.findByAuthorId("u1")).thenReturn(List.of());
        when(userRepositoryPort.findById("author-1")).thenReturn(Optional.of(User.builder().id("author-1").displayName("Autor").build()));
        when(userRepositoryPort.findById("u1")).thenReturn(Optional.of(User.builder().id("u1").savedPosts(List.of()).build()));

        Map<String, Object> result = voteService.getUserVotedPosts("u1");

        assertThat(result.get("voteCount")).isEqualTo(1);
        assertThat((List<?>) result.get("posts")).hasSize(1);
    }

    // ── getPostVoteCount / getCommentVoteCount ──────────────────────────

    @Test
    void getPostVoteCount_countsOnlyPositiveVotes() {
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("post-1", "post")).thenReturn(List.of(
                Vote.builder().value(1).build(),
                Vote.builder().value(1).build(),
                Vote.builder().value(-1).build()
        ));

        assertThat(voteService.getPostVoteCount("post-1")).isEqualTo(2);
    }

    @Test
    void getCommentVoteCount_countsOnlyPositiveVotes() {
        when(voteRepositoryPort.findAllByTargetIdAndTargetType("comment-1", "comment")).thenReturn(List.of(
                Vote.builder().value(1).build(),
                Vote.builder().value(-1).build(),
                Vote.builder().value(-1).build()
        ));

        assertThat(voteService.getCommentVoteCount("comment-1")).isEqualTo(1);
    }

    // ── getUserVote ─────────────────────────────────────────────────────

    @Test
    void getUserVote_returnsTheStoredValue() {
        when(voteRepositoryPort.findByUserIdAndTargetIdAndTargetType("u1", "post-1", "post"))
                .thenReturn(Optional.of(Vote.builder().value(-1).build()));

        assertThat(voteService.getUserVote("u1", "post-1", "post")).isEqualTo(-1);
    }

    @Test
    void getUserVote_returnsZero_whenNoVoteExists() {
        when(voteRepositoryPort.findByUserIdAndTargetIdAndTargetType("u1", "post-1", "post"))
                .thenReturn(Optional.empty());

        assertThat(voteService.getUserVote("u1", "post-1", "post")).isZero();
    }

    // ── cleanupDuplicateVotes ───────────────────────────────────────────

    @Test
    void cleanupDuplicateVotes_removesAllButOnePerUserTargetTypeGroup() {
        Vote dup1 = Vote.builder().id("v1").userId("u1").targetId("post-1").targetType("post").build();
        Vote dup2 = Vote.builder().id("v2").userId("u1").targetId("post-1").targetType("post").build();
        Vote unique = Vote.builder().id("v3").userId("u2").targetId("post-2").targetType("post").build();
        when(voteRepositoryPort.findAll()).thenReturn(List.of(dup1, dup2, unique));

        Map<String, Object> result = voteService.cleanupDuplicateVotes();

        assertThat(result.get("duplicatesRemoved")).isEqualTo(1);
        verify(voteRepositoryPort, times(1)).delete(any());
    }

    @Test
    void cleanupDuplicateVotes_isANoOp_whenThereAreNoDuplicates() {
        Vote v1 = Vote.builder().id("v1").userId("u1").targetId("post-1").targetType("post").build();
        Vote v2 = Vote.builder().id("v2").userId("u2").targetId("post-2").targetType("post").build();
        when(voteRepositoryPort.findAll()).thenReturn(List.of(v1, v2));

        Map<String, Object> result = voteService.cleanupDuplicateVotes();

        assertThat(result.get("duplicatesRemoved")).isEqualTo(0);
        verify(voteRepositoryPort, never()).delete(any());
    }
}
