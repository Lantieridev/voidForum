package com.voidforum.repository;

import com.voidforum.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface VoteRepository extends MongoRepository<Vote, String> {
    Optional<Vote> findByUserIdAndTargetId(String userId, String targetId);
    List<Vote> findAllByTargetId(String targetId); // Para contar los votos
    void deleteAllByTargetId(String targetId);    // Para el borrado en cascada
}