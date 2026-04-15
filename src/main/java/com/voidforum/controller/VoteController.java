package com.voidforum.controller;

import com.voidforum.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{targetId}")
    public ResponseEntity<Void> vote(
            @PathVariable String targetId,
            @RequestParam int value,
            Principal principal) {

        // Buscamos el ID del usuario (o username) según cómo lo manejes
        String userId = principal.getName();
        voteService.toggleVote(targetId, userId, value);

        return ResponseEntity.ok().build();
    }
}