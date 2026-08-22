package com.voidforum.infrastructure.persistence.adapter;

import com.voidforum.domain.model.Vote;
import com.voidforum.domain.port.out.VoteRepositoryPort;
import com.voidforum.infrastructure.persistence.entity.VoteDocument;
import com.voidforum.infrastructure.persistence.mapper.VoteMapper;
import com.voidforum.infrastructure.persistence.mongo.MongoVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VotePersistenceAdapter implements VoteRepositoryPort {

    private final MongoVoteRepository mongoVoteRepository;

    @Override
    public Vote save(Vote vote) {
        VoteDocument doc = VoteMapper.toEntity(vote);
        VoteDocument saved = mongoVoteRepository.save(doc);
        return VoteMapper.toDomain(saved);
    }

    @Override
    public void delete(Vote vote) {
        VoteDocument doc = VoteMapper.toEntity(vote);
        mongoVoteRepository.delete(doc);
    }

    @Override
    public Optional<Vote> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType) {
        return mongoVoteRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)
                .map(VoteMapper::toDomain);
    }

    @Override
    public List<Vote> findAllByTargetIdAndTargetType(String targetId, String targetType) {
        return mongoVoteRepository.findAllByTargetIdAndTargetType(targetId, targetType).stream()
                .map(VoteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByTargetIdAndTargetType(String targetId, String targetType) {
        mongoVoteRepository.deleteAllByTargetIdAndTargetType(targetId, targetType);
    }

    @Override
    public List<Vote> findAllByUserIdAndTargetType(String userId, String targetType) {
        return mongoVoteRepository.findAllByUserIdAndTargetType(userId, targetType).stream()
                .map(VoteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Vote> findAll() {
        return mongoVoteRepository.findAll().stream()
                .map(VoteMapper::toDomain)
                .collect(Collectors.toList());
    }
}
