package com.voidforum.repository;

import com.voidforum.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface VoteRepository extends MongoRepository<Vote, String> {
    Optional<Vote> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    List<Vote> findAllByTargetIdAndTargetType(String targetId, String targetType);
    void deleteAllByTargetIdAndTargetType(String targetId, String targetType);
    List<Vote> findAllByUserIdAndTargetType(String userId, String targetType);
    List<Vote> findAllByUserId(String userId);
}