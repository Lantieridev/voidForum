package com.voidforum.domain.service;

import com.voidforum.domain.model.Post;
import com.voidforum.domain.model.User;
import com.voidforum.domain.model.Vote;
import com.voidforum.domain.port.in.VoteUseCase;
import com.voidforum.domain.port.out.PostRepositoryPort;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.domain.port.out.VoteRepositoryPort;
import com.voidforum.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteUseCase {

    private final VoteRepositoryPort voteRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PostRepositoryPort postRepositoryPort;

    @Override
    @Transactional
    public Map<String, Object> toggleVote(String targetId, String userId, int newValue, String targetType) {
        Optional<Vote> existingVote = voteRepositoryPort.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);

        int previousValue = existingVote.map(Vote::getValue).orElse(0);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getValue() == newValue) {
                voteRepositoryPort.delete(vote);
            } else {
                vote.setValue(newValue);
                voteRepositoryPort.save(vote);
            }
        } else {
            Vote newVote = Vote.builder()
                    .userId(userId)
                    .targetId(targetId)
                    .targetType(targetType)
                    .value(newValue)
                    .build();
            voteRepositoryPort.save(newVote);
        }

        if ("post".equals(targetType)) {
            postRepositoryPort.findById(targetId).ifPresent(post -> {
                long newVoteCount = voteRepositoryPort.findAllByTargetIdAndTargetType(targetId, "post")
                        .stream()
                        .filter(v -> v.getValue() == 1)
                        .count();
                post.setVoteCount((int) newVoteCount);
                postRepositoryPort.save(post);
            });
        }

        int voteCount = getVoteCount(targetId, targetType);
        int userVote = (previousValue == newValue) ? 0 : newValue;

        return Map.of(
                "voteCount", voteCount,
                "userVote", userVote
        );
    }

    @Override
    public Map<String, Object> getUserVotedPosts(String userId) {
        List<Vote> userVotes = voteRepositoryPort.findAllByUserIdAndTargetType(userId, "post");

        List<String> likedPostIds = userVotes.stream()
                .filter(v -> v.getValue() == 1)
                .map(Vote::getTargetId)
                .collect(Collectors.toList());

        List<Post> likedPosts = postRepositoryPort.findAllById(likedPostIds);
        List<Post> userCreatedPosts = postRepositoryPort.findByAuthorId(userId);

        int postCount = userCreatedPosts.size();

        List<PostResponseDto> likedPostDtos = likedPosts.stream()
                .map(post -> {
                    String authorDisplayName = null;
                    if (post.getAuthorId() != null) {
                        var author = userRepositoryPort.findById(post.getAuthorId()).orElse(null);
                        if (author != null) {
                            authorDisplayName = author.getDisplayName();
                        }
                    }
                    return new PostResponseDto(
                            post.getId(),
                            post.getContent(),
                            post.getAuthorUsername(),
                            post.getAuthorId(),
                            post.getTags(),
                            post.getVoteCount(),
                            post.getCommentCount() != null ? post.getCommentCount() : 0,
                            post.getCreatedAt(),
                            post.getSavedCount() != null ? post.getSavedCount() : 0,
                            authorDisplayName
                    );
                })
                .collect(Collectors.toList());

        List<PostResponseDto> userPostDtos = userCreatedPosts.stream()
                .map(post -> {
                    String authorDisplayName = null;
                    if (post.getAuthorId() != null) {
                        var author = userRepositoryPort.findById(post.getAuthorId()).orElse(null);
                        if (author != null) {
                            authorDisplayName = author.getDisplayName();
                        }
                    }
                    return new PostResponseDto(
                            post.getId(),
                            post.getContent(),
                            post.getAuthorUsername(),
                            post.getAuthorId(),
                            post.getTags(),
                            post.getVoteCount(),
                            post.getCommentCount() != null ? post.getCommentCount() : 0,
                            post.getCreatedAt(),
                            post.getSavedCount() != null ? post.getSavedCount() : 0,
                            authorDisplayName
                    );
                })
                .collect(Collectors.toList());

        User user = userRepositoryPort.findById(userId).orElse(null);
        List<PostResponseDto> savedPostDtos = List.of();
        if (user != null && user.getSavedPosts() != null && !user.getSavedPosts().isEmpty()) {
            List<Post> savedPostsList = postRepositoryPort.findAllById(user.getSavedPosts());
            savedPostDtos = savedPostsList.stream()
                    .map(post -> {
                        String authorDisplayName = null;
                        if (post.getAuthorId() != null) {
                            var author = userRepositoryPort.findById(post.getAuthorId()).orElse(null);
                            if (author != null) {
                                authorDisplayName = author.getDisplayName();
                            }
                        }
                        return new PostResponseDto(
                                post.getId(),
                                post.getContent(),
                                post.getAuthorUsername(),
                                post.getAuthorId(),
                                post.getTags() != null ? post.getTags() : List.of(),
                                post.getVoteCount() != null ? post.getVoteCount() : 0,
                                post.getCommentCount() != null ? post.getCommentCount() : 0,
                                post.getCreatedAt(),
                                post.getSavedCount() != null ? post.getSavedCount() : 0,
                                authorDisplayName
                        );
                    })
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

    @Override
    public int getPostVoteCount(String targetId) {
        return getVoteCount(targetId, "post");
    }

    @Override
    public int getCommentVoteCount(String targetId) {
        return getVoteCount(targetId, "comment");
    }

    private int getVoteCount(String targetId, String targetType) {
        List<Vote> votes = voteRepositoryPort.findAllByTargetIdAndTargetType(targetId, targetType);
        return (int) votes.stream().filter(v -> v.getValue() == 1).count();
    }

    @Override
    public int getUserVote(String userId, String targetId, String targetType) {
        return voteRepositoryPort.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)
                .map(Vote::getValue)
                .orElse(0);
    }

    @Override
    public Map<String, Object> cleanupDuplicateVotes() {
        List<Vote> allVotes = voteRepositoryPort.findAll();
        Map<String, List<Vote>> groupedVotes = allVotes.stream()
                .collect(Collectors.groupingBy(v -> v.getUserId() + "_" + v.getTargetId() + "_" + v.getTargetType()));

        int duplicatesRemoved = 0;
        for (Map.Entry<String, List<Vote>> entry : groupedVotes.entrySet()) {
            if (entry.getValue().size() > 1) {
                for (int i = 1; i < entry.getValue().size(); i++) {
                    voteRepositoryPort.delete(entry.getValue().get(i));
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
