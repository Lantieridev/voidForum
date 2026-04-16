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

    public void vote(String targetId, int value, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Vote vote = voteRepository.findByUserIdAndTargetId(user.getId(), targetId)
                .orElse(Vote.builder()
                        .userId(user.getId())
                        .targetId(targetId)
                        .build());

        vote.setValue(value);
        voteRepository.save(vote);
    }

    @Transactional
    public void toggleVote(String targetId, String userId, int newValue) {
        Optional<Vote> existingVote = voteRepository.findByUserIdAndTargetId(userId, targetId);

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
                    .value(newValue)
                    .build();
            voteRepository.save(newVote);
        }

        postRepository.findById(targetId).ifPresent(post -> {
            int newVoteCount = voteRepository.findAllByTargetId(targetId)
                    .stream()
                    .filter(v -> v.getValue() == 1)
                    .mapToInt(Vote::getValue)
                    .sum();
            post.setVoteCount(newVoteCount);
            postRepository.save(post);
        });
    }

    public Map<String, Object> getUserVotedPosts(String userId) {
        List<Vote> userVotes = voteRepository.findAllByUserId(userId);

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
                        post.getCreatedAt(),
                        post.getSavedCount() != null ? post.getSavedCount() : 0
                ))
                .collect(Collectors.toList());

        return Map.of(
                "posts", likedPostDtos,
                "userPosts", userPostDtos,
                "postCount", postCount,
                "voteCount", likedPostIds.size()
        );
    }

    public int getPostVoteCount(String targetId) {
        List<Vote> votes = voteRepository.findAllByTargetId(targetId);
        return votes.stream().filter(v -> v.getValue() == 1).mapToInt(Vote::getValue).sum();
    }
}