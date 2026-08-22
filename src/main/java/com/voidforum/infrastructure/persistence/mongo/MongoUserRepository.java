package com.voidforum.infrastructure.persistence.mongo;

import com.voidforum.infrastructure.persistence.entity.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoUserRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByUsername(String username);
    Optional<UserDocument> findByEmail(String email);

    @Query("{ 'followingIds': ?0 }")
    List<UserDocument> findByFollowingId(String userId);
}
