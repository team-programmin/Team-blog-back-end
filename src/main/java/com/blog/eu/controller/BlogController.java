package com.blog.eu.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.eu.dto.AuthorDTO;
import com.blog.eu.dto.ComentarioDTO;
import com.blog.eu.dto.PostDTO;
import com.blog.eu.exepitons.launch.Invalid;
import com.blog.eu.exepitons.launch.NotFout;
import com.blog.eu.model.Comentario;
import com.blog.eu.model.Post;
import com.blog.eu.model.Role;
import com.blog.eu.model.User;
import com.blog.eu.repo.ComentarioRepository;
import com.blog.eu.repo.PostRepository;
import com.blog.eu.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador principal do blog.
 *
 * Esta classe expõe os endpoints REST para operações relacionadas
 * a posts e comentários, incluindo criação, edição, listagem e exclusão.
 *
 * Regras de acesso:
 * - Posts: apenas usuários com role ADMIN podem criar ou excluir.
 * - Comentários: qualquer usuário autenticado pode criar, editar ou excluir
 * seus próprios comentários.
 *
 * Endpoints disponíveis:
 * - GET /api/blog -> lista posts com paginação
 * - POST /api/blog -> cria novo post (ADMIN)
 * - DELETE /api/blog/{postId}/delete -> exclui post (ADMIN e autor)
 * - POST /api/blog/{postId}/comment -> cria comentário em um post
 * - POST /api/blog/comment/{commentId}/reply -> cria resposta a um comentário
 * - POST /api/blog/comment/{commentId}/edit -> edita comentário (autor)
 * - GET /api/blog/comment/{postId}/all -> lista comentários de um post
 * - DELETE /api/blog/comment/{commentId}/delete -> exclui comentário (autor)
 */
@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final JwtService jwtService;
    private final PostRepository postRepository;
    private final ComentarioRepository comentarioRepository;
    private final com.blog.eu.auth.repository.UserRepository userRepository;

    public BlogController(PostRepository postRepository, JwtService jwtService,
            ComentarioRepository comentarioRepository, com.blog.eu.auth.repository.UserRepository userRepository) {
        this.postRepository = postRepository;
        this.jwtService = jwtService;
        this.comentarioRepository = comentarioRepository;
        this.userRepository = userRepository;
    }

    /**
     * Endpoint para listar posts do blog com paginação.
     *
     * Recebe parâmetros opcionais de página e tamanho para controlar
     * a quantidade de posts retornados.
     *
     * @param page número da página (padrão 0)
     * @param size quantidade de posts por página (padrão 10)
     * @return ResponseEntity com status 200 e a lista de posts da página solicitada
     */

    @GetMapping
    public ResponseEntity<List<PostDTO>> getPosts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            postRepository.findAll(PageRequest.of(page, size))
                .map(this::toPostDTO)
                .getContent()
        );
    }


    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFout("Post não encontrado"));
        return ResponseEntity.ok(toPostDTO(post));
    }


    /**
     * Endpoint para criar um novo post no blog.
     *
     * Requer autenticação via token JWT e role ADMIN. O usuário autenticado
     * será definido como autor do post.
     *
     * @param post objeto Post contendo os dados do novo post
     * @param req  objeto HttpServletRequest usado para extrair o token JWT
     * @return ResponseEntity com status 200 e o post criado se a operação for
     *         bem-sucedida;
     *         ResponseEntity com status 403 se o usuário não tiver permissão
     * @throws RuntimeException se o token estiver ausente ou se o usuário não for
     *                          encontrado
     */

@PostMapping
public ResponseEntity<PostDTO> createPost(@RequestBody Post post, HttpServletRequest req) {
    String header = req.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
        throw new Invalid("Token ausente");
    }

    String role = jwtService.parseRole(header.substring(7));
    if (!Role.ADMIN.name().equals(role)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Long userId = jwtService.parseSubject(header.substring(7));
    User autor = userRepository.findById(userId)
            .orElseThrow(() -> new NotFout("Usuário não encontrado"));

    post.setAuthor(autor);
    Post saved = postRepository.save(post);

    return ResponseEntity.ok(toPostDTO(saved));
}


    /**
     * Endpoint para criar um comentário em um post.
     *
     * Requer autenticação via token JWT. O usuário autenticado será definido
     * como autor do comentário.
     *
     * @param postId  ID do post ao qual o comentário será associado
     * @param comment objeto Comentario contendo os dados do comentário
     * @param req     objeto HttpServletRequest usado para extrair o token JWT
     * @return o comentário salvo no repositório
     * @throws RuntimeException se o post não for encontrado
     */

    @PostMapping("/{postId}/comment")
    public Comentario createComment(@PathVariable Long postId,
            @RequestBody Comentario comment,
            HttpServletRequest req) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFout("Post não encontrado"));

        Long userId = jwtService.parseSubject(req.getHeader("Authorization").substring(7));
        User autor = userRepository.findById(userId).orElseThrow();

        comment.setPost(post);
        comment.setAutor(autor);
        comment.setDataCriacao(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        return comentarioRepository.save(comment);
    }
    private PostDTO toPostDTO(Post post) {
    return new PostDTO(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        new AuthorDTO(
            post.getAuthor().getId(),
            post.getAuthor().getDisplayName(),
            post.getAuthor().getAvatarUrl()
        ),
        post.getContent() != null ? post.getContent().toString() : null,
        comentarioRepository.findByPostAndParentIsNull(post)
            .stream()
            .map(this::toDTO)
            .toList()
    );
}

    /**
     * Endpoint para criar uma resposta a um comentário existente.
     *
     * Requer autenticação via token JWT. O usuário autenticado será definido
     * como autor da resposta.
     *
     * @param commentId ID do comentário ao qual a resposta será vinculada
     * @param reply     objeto Comentario contendo os dados da resposta
     * @param req       objeto HttpServletRequest usado para extrair o token JWT
     * @return ResponseEntity com status 200 e o comentário resposta salvo
     * @throws RuntimeException se o comentário pai não for encontrado
     */

    @PostMapping("/comment/{commentId}/reply")
public ResponseEntity<ComentarioDTO> createCommentReply(@PathVariable Long commentId,
        @RequestBody Comentario reply,
        HttpServletRequest req) {

    Comentario parentComment = comentarioRepository.findById(commentId)
            .orElseThrow(() -> new NotFout("Comentário não encontrado"));

    Long userId = jwtService.parseSubject(req.getHeader("Authorization").substring(7));
    User autor = userRepository.findById(userId).orElseThrow();

    reply.setPost(parentComment.getPost());
    reply.setParent(parentComment);
    reply.setAutor(autor);
    reply.setDataCriacao(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

    Comentario saved = comentarioRepository.save(reply);
    return ResponseEntity.ok(toDTO(saved));
}


    /**
     * Endpoint para editar um comentário existente.
     *
     * Requer autenticação via token JWT. Apenas o autor do comentário
     * pode realizar a edição.
     *
     * @param commentId ID do comentário a ser editado
     * @param newText   novo texto que substituirá o conteúdo do comentário
     * @param req       objeto HttpServletRequest usado para extrair o token JWT
     * @return ResponseEntity com status 200 e o comentário atualizado se a edição
     *         for bem-sucedida;
     *         ResponseEntity com status 403 se o usuário não for o autor
     * @throws RuntimeException se o comentário não for encontrado
     */
    @PostMapping("/comment/{commentId}/edit")
public ResponseEntity<ComentarioDTO> editComment(@PathVariable Long commentId,
        @RequestBody String newText,
        HttpServletRequest req) {

    Comentario comment = comentarioRepository.findById(commentId)
            .orElseThrow(() -> new NotFout("Comentário não encontrado"));

    Long userId = jwtService.parseSubject(req.getHeader("Authorization").substring(7));
    if (!comment.getAutor().getId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    comment.setTexto(newText);
    comment.setModificado(true);

    Comentario saved = comentarioRepository.save(comment);
    return ResponseEntity.ok(toDTO(saved));
}


    /**
     * Endpoint para listar todos os comentários de um post específico.
     *
     * Recebe o ID de um post e retorna todos os comentários associados a ele.
     *
     * @param PostId ID do post cujos comentários devem ser listados
     * @return ResponseEntity com status 200 e a lista de comentários do post
     * @throws RuntimeException se o post não for encontrado
     */
    @GetMapping("/comment/{postId}/all")
    public List<ComentarioDTO> getAll(@PathVariable Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow();

        return comentarioRepository
                .findByPostAndParentIsNull(post)
                .stream()
                .map(this:: toDTO)
                .toList();
    }
    private ComentarioDTO toDTO(Comentario c) {
    return new ComentarioDTO(
        c.getId(),
        c.getTexto(),
        new AuthorDTO(
            c.getAutor().getId(),
            c.getAutor().getDisplayName(),
            c.getAutor().getAvatarUrl()
        ),
        c.getDataCriacao(),
        c.getModificado(),
        c.getRespostas()
            .stream()
            .map(this::toDTO)
            .toList()
    );
}


    /**
     * Endpoint para excluir um comentário de um post.
     *
     * Requer autenticação via token JWT. Apenas o autor do comentário
     * pode realizar a exclusão.
     *
     * @param commentId ID do comentário a ser deletado
     * @param req       objeto HttpServletRequest usado para extrair o token JWT
     * @return ResponseEntity com status 200 e mensagem de sucesso se o comentário
     *         for deletado;
     *         ResponseEntity com status 403 se o usuário não for o autor
     * @throws RuntimeException se o comentário não for encontrado
     */
    @DeleteMapping("/comment/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId,
            HttpServletRequest req) {

        Comentario comment = comentarioRepository.findById(commentId)
                .orElseThrow(() -> new NotFout("Comentário não encontrado"));

        Long userId = jwtService.parseSubject(req.getHeader("Authorization").substring(7));
        if (!comment.getAutor().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
        }

        comentarioRepository.delete(comment);

        return ResponseEntity.ok("Comentário deletado com sucesso");
    }

    /**
     * Endpoint para excluir um post do blog.
     *
     * Requer autenticação via token JWT e role ADMIN.
     * Apenas o autor do post pode realizar a exclusão.
     *
     * @param postId ID do post a ser deletado
     * @param req    objeto HttpServletRequest usado para extrair o token JWT
     * @return ResponseEntity com status 200 e mensagem de sucesso se o post for
     *         deletado
     * @throws RuntimeException se o token estiver ausente, se o usuário não for
     *                          ADMIN,
     *                          se o post não existir ou se o usuário não for o
     *                          autor
     */
    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<String> deletePost(@PathVariable Long postId,
            HttpServletRequest req) {

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new Invalid("Token ausente");
        }

        if (!Role.ADMIN.name().equals(jwtService.parseRole(header.substring(7)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFout("Post não encontrado"));

        Long userId = jwtService.parseSubject(req.getHeader("Authorization").substring(7));
        if (!post.getAuthor().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
        }

        postRepository.delete(post);

        return ResponseEntity.ok("Post deletado com sucesso");
    }
}
