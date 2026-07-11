package com.voidforum.controller;

import com.voidforum.exception.UnauthorizedException;
import com.voidforum.model.User;
import com.voidforum.repository.UserRepository;
import com.voidforum.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final UserRepository userRepository;

    @PostMapping("/{targetId}")
    public ResponseEntity<?> vote(
            @PathVariable String targetId,
            @RequestParam int value,
            @RequestParam(defaultValue = "post") String targetType,
            Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("Token inválido o expirado"));
        Map<String, Object> result = voteService.toggleVote(targetId, user.getId(), value, targetType);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserVotes(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("Token inválido o expirado"));
        Map<String, Object> response = voteService.getUserVotedPosts(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{targetId}/count")
    public ResponseEntity<?> getVoteCount(
            @PathVariable String targetId,
            @RequestParam(defaultValue = "post") String targetType) {
        int count = "comment".equals(targetType)
                ? voteService.getCommentVoteCount(targetId)
                : voteService.getPostVoteCount(targetId);
        return ResponseEntity.ok(Map.of("votes", count));
    }

    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanupDuplicateVotes() {
        Map<String, Object> result = voteService.cleanupDuplicateVotes();
        return ResponseEntity.ok(result);
    }
}
