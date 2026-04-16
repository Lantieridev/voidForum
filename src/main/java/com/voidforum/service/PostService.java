package com.voidforum.service;

import com.voidforum.dto.PostCreateDto;
import com.voidforum.dto.PostResponseDto;
import com.voidforum.model.Post;
import com.voidforum.model.User;
import com.voidforum.model.Vote;
import com.voidforum.repository.CommentRepository;
import com.voidforum.repository.PostRepository;
import com.voidforum.repository.UserRepository;
import com.voidforum.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    public PostResponseDto createPost(PostCreateDto request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Post post = Post.builder()
                .content(request.getContent())
                .tags(request.getTags())
                .authorId(author.getId())
                .authorUsername(author.getUsername())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponseDto(postRepository.save(post));
    }

    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PostResponseDto> searchPosts(String query) {
        return postRepository.searchPosts(query).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PostResponseDto> searchByTag(String tag) {
        return postRepository.searchByTag(tag).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PostResponseDto> searchByAuthor(String username) {
        return postRepository.searchByAuthor(username).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PostResponseDto> searchByContent(String content) {
        return postRepository.searchByContent(content).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    public PostResponseDto updatePost(String id, PostCreateDto postRequest, String currentUsername) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        // Seguridad: Solo el autor edita
        if (!post.getAuthorUsername().equals(currentUsername)) {
            throw new RuntimeException("No tenés permiso para editar este post");
        }

        // Actualización de campos
        post.setContent(postRequest.getContent());
        post.setTags(postRequest.getTags());

        Post updatedPost = postRepository.save(post);
        return mapToResponseDto(updatedPost);
    }

    public void deletePost(String postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        // Seguridad: Solo el dueño borra
        if (!post.getAuthorUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para borrar este post");
        }

        // --- BORRADO EN CASCADA ---
        voteRepository.deleteAllByTargetId(postId);      // Borra votos
        commentRepository.deleteAllByPostId(postId);     // Borra comentarios
        postRepository.deleteById(postId);               // Borra el post
    }

    private PostResponseDto mapToResponseDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getContent() != null ? post.getContent() : "",
                post.getAuthorUsername() != null ? post.getAuthorUsername() : "Unknown",
                post.getAuthorId() != null ? post.getAuthorId() : "",
                post.getTags() != null ? post.getTags() : java.util.List.of(),
                post.getVoteCount() != null ? post.getVoteCount() : 0,
                post.getCreatedAt() != null ? post.getCreatedAt() : java.time.LocalDateTime.now()
        );
    }
}