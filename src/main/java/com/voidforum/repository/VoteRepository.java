package com.voidforum.repository;

import com.voidforum.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends MongoRepository<Vote, String> {
    Optional<Vote> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    void deleteByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    long countByTargetIdAndVoteType(String targetId, String voteType);
}