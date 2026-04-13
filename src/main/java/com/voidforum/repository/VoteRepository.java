package com.voidforum.repository;

import com.voidforum.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface VoteRepository extends MongoRepository<Vote, String> {
    // Para verificar si un usuario ya votó un post/comentario específico y evitar votos duplicados
    Optional<Vote> findByUserIdAndTargetId(String userId, String targetId);
}