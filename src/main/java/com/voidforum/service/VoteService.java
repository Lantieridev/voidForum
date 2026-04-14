package com.voidforum.service;

import com.voidforum.model.Vote;
import com.voidforum.model.User;
import com.voidforum.repository.VoteRepository;
import com.voidforum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    public void vote(String targetId, int value, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscamos si ya existe un voto de este usuario para este post/comentario
        Vote vote = voteRepository.findByUserIdAndTargetId(user.getId(), targetId)
                .orElse(Vote.builder()
                        .userId(user.getId())
                        .targetId(targetId)
                        .build());

        vote.setValue(value); // Actualizamos el valor (1 o -1)
        voteRepository.save(vote);
    }
}