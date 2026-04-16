package com.voidforum.controller;

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
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Map<String, Object> result = voteService.toggleVote(targetId, user.getId(), value, targetType);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserVotes(Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Map<String, Object> response = voteService.getUserVotedPosts(user.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{targetId}/count")
    public ResponseEntity<?> getVoteCount(
            @PathVariable String targetId,
            @RequestParam(defaultValue = "post") String targetType) {
        try {
            int count;
            if ("comment".equals(targetType)) {
                count = voteService.getCommentVoteCount(targetId);
            } else {
                count = voteService.getPostVoteCount(targetId);
            }
            return ResponseEntity.ok(Map.of("votes", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}