package com.voidforum.service;

import com.voidforum.domain.model.User;
import com.voidforum.domain.port.in.CommentUseCase;
import com.voidforum.domain.port.in.PostUseCase;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.domain.service.UserServiceImpl;
import com.voidforum.dto.UpdateNotificationsDto;
import com.voidforum.dto.UpdateProfileDto;
import com.voidforum.exception.ConflictException;
import com.voidforum.exception.ResourceNotFoundException;
import com.voidforum.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepositoryPort userRepositoryPort = mock(UserRepositoryPort.class);
    private final BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
    private final PostUseCase postUseCase = mock(PostUseCase.class);
    private final CommentUseCase commentUseCase = mock(CommentUseCase.class);
    private final UserServiceImpl userService =
            new UserServiceImpl(userRepositoryPort, passwordEncoder, postUseCase, commentUseCase);

    private User user(String id, String username) {
        return User.builder().id(id).username(username).password("hashed").build();
    }

    // ── findByUsername / findById ──────────────────────────────────────

    @Test
    void findByUsername_throwsResourceNotFound_whenNoSuchUser() {
        when(userRepositoryPort.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("ghost"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_throwsResourceNotFound_whenNoSuchUser() {
        when(userRepositoryPort.findById("id-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById("id-404"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateProfile ───────────────────────────────────────────────────

    @Test
    void updateProfile_updatesDisplayNameAndBio() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(userRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateProfile("martin", new UpdateProfileDto(null, null, "Martín", "Fan de rock"));

        assertThat(result.getDisplayName()).isEqualTo("Martín");
        assertThat(result.getBio()).isEqualTo("Fan de rock");
    }

    @Test
    void updateProfile_changesUsername_whenTheNewOneIsFree() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(userRepositoryPort.findByUsername("martin2")).thenReturn(Optional.empty());
        when(userRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateProfile("martin", new UpdateProfileDto("martin2", null, null, null));

        assertThat(result.getUsername()).isEqualTo("martin2");
    }

    @Test
    void updateProfile_throwsConflict_whenTheNewUsernameIsTaken() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(userRepositoryPort.findByUsername("taken")).thenReturn(Optional.of(user("u2", "taken")));

        assertThatThrownBy(() -> userService.updateProfile("martin", new UpdateProfileDto("taken", null, null, null)))
                .isInstanceOf(ConflictException.class);
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void updateProfile_throwsConflict_whenTheNewEmailIsTaken() {
        User u = user("u1", "martin");
        u.setEmail("martin@example.com");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(userRepositoryPort.findByEmail("taken@example.com")).thenReturn(Optional.of(user("u2", "other")));

        assertThatThrownBy(() ->
                userService.updateProfile("martin", new UpdateProfileDto(null, "taken@example.com", null, null)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void updateProfile_doesNotTreatKeepingTheSameUsernameAsAConflict() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(userRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.updateProfile("martin", new UpdateProfileDto("martin", null, "Martín", null));

        verify(userRepositoryPort, times(1)).findByUsername("martin");
    }

    @Test
    void updateProfile_rejectsABioOver280Characters() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        String longBio = "a".repeat(281);

        assertThatThrownBy(() -> userService.updateProfile("martin", new UpdateProfileDto(null, null, null, longBio)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(userRepositoryPort, never()).save(any());
    }

    // ── changePassword ──────────────────────────────────────────────────

    @Test
    void changePassword_updatesToTheNewEncodedPassword() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("old", "hashed")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("new-hashed");
        when(userRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.changePassword("martin", "old", "new");

        assertThat(result.getPassword()).isEqualTo("new-hashed");
    }

    @Test
    void changePassword_throwsUnauthorized_whenTheCurrentPasswordIsWrong() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword("martin", "wrong", "new"))
                .isInstanceOf(UnauthorizedException.class);
        verify(userRepositoryPort, never()).save(any());
    }

    // ── updateNotifications ─────────────────────────────────────────────

    @Test
    void updateNotifications_setsAllThreeFlags() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(userRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateNotifications("martin", new UpdateNotificationsDto(false, true, false));

        assertThat(result.isNotifyLikes()).isFalse();
        assertThat(result.isNotifyComments()).isTrue();
        assertThat(result.isNotifyMentions()).isFalse();
    }

    // ── deleteAccount ───────────────────────────────────────────────────

    @Test
    void deleteAccount_anonymizesTheUserAndCascadesToPostsAndComments() {
        User u = user("u1", "martin");
        u.setEmail("martin@example.com");
        u.setDisplayName("Martín");
        u.setBio("bio");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pw", "hashed")).thenReturn(true);
        when(userRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.deleteAccount("martin", "pw");

        verify(postUseCase).anonymizeUserPosts(eq("martin"), argThat(newName -> newName.startsWith("[deleted]-")));
        verify(commentUseCase).anonymizeUserComments(eq("martin"), argThat(newName -> newName.startsWith("[deleted]-")));
        assertThat(u.getUsername()).startsWith("[deleted]-");
        assertThat(u.getEmail()).endsWith("@deleted.local");
        assertThat(u.getDisplayName()).isNull();
        assertThat(u.getBio()).isNull();
        assertThat(u.getPassword()).isNull();
    }

    @Test
    void deleteAccount_throwsUnauthorized_whenThePasswordIsWrong() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteAccount("martin", "wrong"))
                .isInstanceOf(UnauthorizedException.class);
        verify(postUseCase, never()).anonymizeUserPosts(any(), any());
        verify(commentUseCase, never()).anonymizeUserComments(any(), any());
    }

    // ── follow / unfollow ───────────────────────────────────────────────

    @Test
    void follow_addsTheTargetAndIncrementsBothCounts() {
        User me = user("u1", "martin");
        me.setFollowingIds(new ArrayList<>());
        User target = user("u2", "other");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));
        when(userRepositoryPort.findById("u2")).thenReturn(Optional.of(target));

        userService.follow("martin", "u2");

        assertThat(me.getFollowingIds()).containsExactly("u2");
        assertThat(me.getFollowingCount()).isEqualTo(1);
        assertThat(target.getFollowerCount()).isEqualTo(1);
        verify(userRepositoryPort).save(me);
        verify(userRepositoryPort).save(target);
    }

    @Test
    void follow_rejectsFollowingYourself() {
        User me = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));
        when(userRepositoryPort.findById("u1")).thenReturn(Optional.of(me));

        assertThatThrownBy(() -> userService.follow("martin", "u1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void follow_throwsConflict_whenAlreadyFollowing() {
        User me = user("u1", "martin");
        me.setFollowingIds(new ArrayList<>(List.of("u2")));
        User target = user("u2", "other");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));
        when(userRepositoryPort.findById("u2")).thenReturn(Optional.of(target));

        assertThatThrownBy(() -> userService.follow("martin", "u2"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void unfollow_removesTheTargetAndDecrementsBothCounts() {
        User me = user("u1", "martin");
        me.setFollowingIds(new ArrayList<>(List.of("u2")));
        me.setFollowingCount(1);
        User target = user("u2", "other");
        target.setFollowerCount(1);
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));
        when(userRepositoryPort.findById("u2")).thenReturn(Optional.of(target));

        userService.unfollow("martin", "u2");

        assertThat(me.getFollowingIds()).isEmpty();
        assertThat(me.getFollowingCount()).isZero();
        assertThat(target.getFollowerCount()).isZero();
    }

    @Test
    void unfollow_throwsIllegalArgument_whenNotCurrentlyFollowing() {
        User me = user("u1", "martin");
        me.setFollowingIds(new ArrayList<>());
        User target = user("u2", "other");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));
        when(userRepositoryPort.findById("u2")).thenReturn(Optional.of(target));

        assertThatThrownBy(() -> userService.unfollow("martin", "u2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unfollow_neverGoesBelowZero_evenIfCountsWereAlreadyInconsistent() {
        User me = user("u1", "martin");
        me.setFollowingIds(new ArrayList<>(List.of("u2")));
        me.setFollowingCount(0);
        User target = user("u2", "other");
        target.setFollowerCount(0);
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));
        when(userRepositoryPort.findById("u2")).thenReturn(Optional.of(target));

        userService.unfollow("martin", "u2");

        assertThat(me.getFollowingCount()).isZero();
        assertThat(target.getFollowerCount()).isZero();
    }

    // ── isFollowing / getFollowingIds ───────────────────────────────────

    @Test
    void isFollowing_trueWhenTargetIsInTheList() {
        User me = user("u1", "martin");
        me.setFollowingIds(new ArrayList<>(List.of("u2")));
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));

        assertThat(userService.isFollowing("martin", "u2")).isTrue();
    }

    @Test
    void isFollowing_falseWhenFollowingIdsIsNull() {
        User me = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));

        assertThat(userService.isFollowing("martin", "u2")).isFalse();
    }

    @Test
    void getFollowingIds_returnsAnEmptyListInsteadOfNull() {
        User me = user("u1", "martin");
        when(userRepositoryPort.findByUsername("martin")).thenReturn(Optional.of(me));

        assertThat(userService.getFollowingIds("martin")).isEmpty();
    }

    // ── savePost / unsavePost / getSavedPosts ───────────────────────────

    @Test
    void savePost_addsThePostOnlyOnce() {
        User u = user("u1", "martin");
        u.setSavedPosts(new ArrayList<>());
        when(userRepositoryPort.findById("u1")).thenReturn(Optional.of(u));
        when(userRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.savePost("u1", "post-1");
        User afterSecondCall = userService.savePost("u1", "post-1");

        assertThat(afterSecondCall.getSavedPosts()).containsExactly("post-1");
        verify(userRepositoryPort, times(1)).save(any());
    }

    @Test
    void unsavePost_removesAnExistingSavedPost() {
        User u = user("u1", "martin");
        u.setSavedPosts(new ArrayList<>(List.of("post-1")));
        when(userRepositoryPort.findById("u1")).thenReturn(Optional.of(u));
        when(userRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.unsavePost("u1", "post-1");

        assertThat(result.getSavedPosts()).isEmpty();
    }

    @Test
    void unsavePost_isANoOp_whenThePostWasNotSaved() {
        User u = user("u1", "martin");
        u.setSavedPosts(new ArrayList<>());
        when(userRepositoryPort.findById("u1")).thenReturn(Optional.of(u));

        userService.unsavePost("u1", "never-saved");

        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void getSavedPosts_returnsAnEmptyListInsteadOfNull_whenNeverSet() {
        User u = user("u1", "martin");
        when(userRepositoryPort.findById("u1")).thenReturn(Optional.of(u));

        assertThat(userService.getSavedPosts("u1")).isEmpty();
    }
}
