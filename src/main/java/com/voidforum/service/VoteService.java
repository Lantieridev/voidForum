package com.voidforum.service;

import com.voidforum.dto.PostResponseDto;
import com.voidforum.model.Vote;
import com.voidforum.model.Post;
import com.voidforum.model.User;
import com.voidforum.repository.VoteRepository;
import com.voidforum.repository.UserRepository;
import com.voidforum.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void vote(String targetId, int value, String username, String targetType) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Vote vote = voteRepository.findByUserIdAndTargetIdAndTargetType(user.getId(), targetId, targetType)
                .orElse(Vote.builder()
                        .userId(user.getId())
                        .targetId(targetId)
                        .targetType(targetType)
                        .build());

        vote.setValue(value);
        voteRepository.save(vote);
    }

    @Transactional
    public Map<String, Object> toggleVote(String targetId, String userId, int newValue, String targetType) {
        Optional<Vote> existingVote = voteRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getValue() == newValue) {
                voteRepository.delete(vote);
            } else {
                vote.setValue(newValue);
                voteRepository.save(vote);
            }
        } else {
            Vote newVote = Vote.builder()
                    .userId(userId)
                    .targetId(targetId)
                    .targetType(targetType)
                    .value(newValue)
                    .build();
            voteRepository.save(newVote);
        }

        // Actualizar voto en post si es targetType = post
        if ("post".equals(targetType)) {
            postRepository.findById(targetId).ifPresent(post -> {
                long newVoteCount = voteRepository.findAllByTargetIdAndTargetType(targetId, "post")
                        .stream()
                        .filter(v -> v.getValue() == 1)
                        .count();
                post.setVoteCount((int) newVoteCount);
                postRepository.save(post);
            });
        }

        int voteCount = getVoteCount(targetId, targetType);
        int userVote = existingVote.map(v -> {
            if (v.getValue() == newValue) return 0;
            return newValue;
        }).orElse(newValue);

        return Map.of(
            "voteCount", voteCount,
            "userVote", userVote
        );
    }

    public Map<String, Object> getUserVotedPosts(String userId) {
        List<Vote> userVotes = voteRepository.findAllByUserIdAndTargetType(userId, "post");

        List<String> likedPostIds = userVotes.stream()
                .filter(v -> v.getValue() == 1)
                .map(Vote::getTargetId)
                .collect(Collectors.toList());

        List<Post> likedPosts = postRepository.findAllById(likedPostIds);

        List<Post> userCreatedPosts = postRepository.findByAuthorId(userId);

        int postCount = userCreatedPosts.size();

        List<PostResponseDto> likedPostDtos = likedPosts.stream()
                .map(post -> new PostResponseDto(
                        post.getId(),
                        post.getContent(),
                        post.getAuthorUsername(),
                        post.getAuthorId(),
                        post.getTags(),
                        post.getVoteCount(),
                        post.getCommentCount() != null ? post.getCommentCount() : 0,
                        post.getCreatedAt(),
                        post.getSavedCount() != null ? post.getSavedCount() : 0
                ))
                .collect(Collectors.toList());

        List<PostResponseDto> userPostDtos = userCreatedPosts.stream()
                .map(post -> new PostResponseDto(
                        post.getId(),
                        post.getContent(),
                        post.getAuthorUsername(),
                        post.getAuthorId(),
                        post.getTags(),
                        post.getVoteCount(),
                        post.getCommentCount() != null ? post.getCommentCount() : 0,
                        post.getCreatedAt(),
                        post.getSavedCount() != null ? post.getSavedCount() : 0
                ))
                .collect(Collectors.toList());

        User user = userRepository.findById(userId).orElse(null);
        List<PostResponseDto> savedPostDtos = List.of();
        if (user != null && user.getSavedPosts() != null && !user.getSavedPosts().isEmpty()) {
            List<Post> savedPostsList = postRepository.findAllById(user.getSavedPosts());
            savedPostDtos = savedPostsList.stream()
                .map(post -> new PostResponseDto(
                    post.getId(),
                    post.getContent(),
                    post.getAuthorUsername(),
                    post.getAuthorId(),
                    post.getTags() != null ? post.getTags() : List.of(),
                    post.getVoteCount() != null ? post.getVoteCount() : 0,
                    post.getCommentCount() != null ? post.getCommentCount() : 0,
                    post.getCreatedAt(),
                    post.getSavedCount() != null ? post.getSavedCount() : 0
                ))
                .collect(Collectors.toList());
        }

        return Map.of(
                "posts", likedPostDtos,
                "userPosts", userPostDtos,
                "postCount", postCount,
                "voteCount", likedPostIds.size(),
                "savedPosts", savedPostDtos
        );
    }

    public int getPostVoteCount(String targetId) {
        return getVoteCount(targetId, "post");
    }

    public int getCommentVoteCount(String targetId) {
        return getVoteCount(targetId, "comment");
    }

    private int getVoteCount(String targetId, String targetType) {
        List<Vote> votes = voteRepository.findAllByTargetIdAndTargetType(targetId, targetType);
        return (int) votes.stream().filter(v -> v.getValue() == 1).count();
    }

    public int getUserVote(String userId, String targetId, String targetType) {
        return voteRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)
                .map(Vote::getValue)
                .orElse(0);
    }

    public Map<String, Object> cleanupDuplicateVotes() {
        List<Vote> allVotes = voteRepository.findAll();
        Map<String, List<Vote>> groupedVotes = allVotes.stream()
                .collect(Collectors.groupingBy(v -> v.getUserId() + "_" + v.getTargetId() + "_" + v.getTargetType()));

        int duplicatesRemoved = 0;
        for (Map.Entry<String, List<Vote>> entry : groupedVotes.entrySet()) {
            if (entry.getValue().size() > 1) {
                Vote keep = entry.getValue().get(0);
                for (int i = 1; i < entry.getValue().size(); i++) {
                    voteRepository.delete(entry.getValue().get(i));
                    duplicatesRemoved++;
                }
            }
        }

        return Map.of(
                "duplicatesRemoved", duplicatesRemoved,
                "message", "Votos duplicados limpiados exitosamente"
        );
    }
}