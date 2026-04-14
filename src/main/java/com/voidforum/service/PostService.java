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
                .title(request.title())
                .content(request.content())
                .tags(request.tags())
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
        // Calculamos el puntaje total sumando los valores de los votos vinculados
        int score = voteRepository.findAllByTargetId(post.getId())
                .stream()
                .mapToInt(Vote::getValue)
                .sum();

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorUsername(),
                post.getTags(),
                score, // <--- El contador dinámico
                post.getCreatedAt()
        );
    }
}