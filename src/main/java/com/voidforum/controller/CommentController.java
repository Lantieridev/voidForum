package com.voidforum.controller;

import com.voidforum.model.Comment;
import com.voidforum.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> create(@RequestBody Comment comment) {
        return ResponseEntity.status(201).body(commentService.addComment(comment));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getByPost(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
}