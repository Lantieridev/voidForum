package com.voidforum.infrastructure.persistence.mongo;

import com.voidforum.infrastructure.persistence.entity.VoteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoVoteRepository extends MongoRepository<VoteDocument, String> {
    Optional<VoteDocument> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    List<VoteDocument> findAllByTargetIdAndTargetType(String targetId, String targetType);
    void deleteAllByTargetIdAndTargetType(String targetId, String targetType);
    List<VoteDocument> findAllByUserIdAndTargetType(String userId, String targetType);
}
