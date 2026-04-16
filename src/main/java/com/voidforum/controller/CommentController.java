package com.voidforum.controller;

import com.voidforum.dto.CommentCreateDto;
import com.voidforum.dto.CommentResponseDto;
import com.voidforum.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentCreateDto request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(201).body(commentService.createComment(request, username));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPost(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    // Asegurate de que el @RequestMapping arriba de la clase sea "/api/comments"

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable String id,
            @RequestBody CommentCreateDto commentRequest,
            Principal principal) {

        // Llamamos al service que ya corregimos antes
        return ResponseEntity.ok(commentService.updateComment(id, commentRequest, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        commentService.deleteComment(id, username);
        return ResponseEntity.noContent().build();
    }
}