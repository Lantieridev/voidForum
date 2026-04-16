package com.voidforum.repository;

import com.voidforum.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface VoteRepository extends MongoRepository<Vote, String> {
    Optional<Vote> findByUserIdAndTargetId(String userId, String targetId);
    List<Vote> findAllByTargetId(String targetId);
    void deleteAllByTargetId(String targetId);
    List<Vote> findAllByUserId(String userId);
}