package com.voidforum.service;

import com.voidforum.model.Vote;
import com.voidforum.model.User;
import com.voidforum.repository.VoteRepository;
import com.voidforum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

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
    public void toggleVote(String targetId, String userId, int newValue) {
        // Buscamos si ya existe un voto de este usuario para este post
        Optional<Vote> existingVote = voteRepository.findByUserIdAndTargetId(userId, targetId);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getValue() == newValue) {
                // Caso 1: El usuario tocó el mismo botón -> BORRAMOS EL VOTO
                voteRepository.delete(vote);
            } else {
                // Caso 2: El usuario cambió de parecer (de +1 a -1 o viceversa) -> ACTUALIZAMOS
                vote.setValue(newValue);
                voteRepository.save(vote);
            }
        } else {
            // Caso 3: No hay voto previo -> CREAMOS UNO NUEVO
            Vote newVote = Vote.builder()
                    .userId(userId)
                    .targetId(targetId)
                    .value(newValue)
                    .build();
            voteRepository.save(newVote);
        }
    }
}