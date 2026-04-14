package com.voidforum.controller;

import com.voidforum.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{targetId}")
    public ResponseEntity<String> vote(
            @PathVariable String targetId,
            @RequestParam int value) { // Mandamos 1 o -1

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        voteService.vote(targetId, value, username);

        return ResponseEntity.ok("Voto registrado correctamente");
    }
}