package com.voidforum.controller;

import com.voidforum.domain.model.User;
import com.voidforum.domain.port.in.UserUseCase;
import com.voidforum.domain.port.in.VoteUseCase;
import com.voidforum.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteUseCase voteUseCase;
    private final UserUseCase userUseCase;

    @PostMapping("/{targetId}")
    public ResponseEntity<?> vote(
            @PathVariable String targetId,
            @RequestParam int value,
            @RequestParam(defaultValue = "post") String targetType,
            Principal principal) {
        User user;
        try {
            user = userUseCase.findByUsername(principal.getName());
        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido o expirado");
        }
        Map<String, Object> result = voteUseCase.toggleVote(targetId, user.getId(), value, targetType);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserVotes(Principal principal) {
        User user;
        try {
            user = userUseCase.findByUsername(principal.getName());
        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido o expirado");
        }
        Map<String, Object> response = voteUseCase.getUserVotedPosts(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{targetId}/count")
    public ResponseEntity<?> getVoteCount(
            @PathVariable String targetId,
            @RequestParam(defaultValue = "post") String targetType) {
        int count = "comment".equals(targetType)
                ? voteUseCase.getCommentVoteCount(targetId)
                : voteUseCase.getPostVoteCount(targetId);
        return ResponseEntity.ok(Map.of("votes", count));
    }

    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanupDuplicateVotes() {
        Map<String, Object> result = voteUseCase.cleanupDuplicateVotes();
        return ResponseEntity.ok(result);
    }
}
