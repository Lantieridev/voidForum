package com.voidforum.domain.port.out;

import com.voidforum.domain.model.Vote;

import java.util.List;
import java.util.Optional;

public interface VoteRepositoryPort {
    Vote save(Vote vote);
    void delete(Vote vote);
    Optional<Vote> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    List<Vote> findAllByTargetIdAndTargetType(String targetId, String targetType);
    void deleteAllByTargetIdAndTargetType(String targetId, String targetType);
    List<Vote> findAllByUserIdAndTargetType(String userId, String targetType);
    List<Vote> findAll();
}
