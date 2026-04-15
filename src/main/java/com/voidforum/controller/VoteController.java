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
            Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            voteService.toggleVote(targetId, user.getId(), value);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}