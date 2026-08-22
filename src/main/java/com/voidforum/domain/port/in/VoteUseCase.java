package com.voidforum.domain.port.in;

import java.util.Map;

public interface VoteUseCase {
    Map<String, Object> toggleVote(String targetId, String userId, int newValue, String targetType);
    Map<String, Object> getUserVotedPosts(String userId);
    int getPostVoteCount(String targetId);
    int getCommentVoteCount(String targetId);
    int getUserVote(String userId, String targetId, String targetType);
    Map<String, Object> cleanupDuplicateVotes();
}
