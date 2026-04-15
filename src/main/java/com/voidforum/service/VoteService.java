package com.voidforum.service;

import com.voidforum.model.Vote;
import com.voidforum.model.User;
import com.voidforum.repository.VoteRepository;
import com.voidforum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

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
    }
}