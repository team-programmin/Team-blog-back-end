package com.blog.eu.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Entidade JPA que representa um post publicado por um {@link User}.
 *
 * Cada post possui título, conteúdo e um autor associado. Além disso,
 * pode conter uma lista de comentários vinculados, permitindo interação
 * entre usuários.
 *
 * Relacionamentos:
 * - Muitos posts pertencem a um único {@link User} (autor)
 * - Um post pode ter vários {@link Comentario} associados
 *
 * Campos principais:
 * - id: identificador único do post
 * - title: título do post (obrigatório)
 * - content: conteúdo textual do post
 * - author: usuário autor do post (obrigatório)
 * - comments: lista de comentários associados ao post
 *
 * Essa entidade é fundamental para o funcionamento do sistema de blog,
 * permitindo a criação, exibição e interação em posts.
 *
 * @author Luis
 */
@Entity
@Table(name = "posts")
public class Post {

    /** Identificador único do post */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título do post (obrigatório) */
    @Column(nullable=false)
    private String title;

    /** Conteúdo textual do post */
    @Column(columnDefinition="TEXT")
    private String content;

    /** Usuário autor do post (obrigatório) */
    @ManyToOne
    @JoinColumn(name="author_id", nullable=false)
    private User author;

    /** Lista de comentários associados ao post */
    @OneToMany(mappedBy="post", cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonManagedReference
    private List<Comentario> comments = new ArrayList<>();

    /** Construtor padrão exigido pelo JPA */
    public Post() {}

    /**
     * Construtor completo para inicializar todos os campos do post.
     *
     * @param id identificador único
     * @param title título do post
     * @param content conteúdo textual
     * @param author usuário autor do post
     * @param comments lista de comentários associados
     */
    public Post(Long id, String title, String content, User author, ArrayList<Comentario> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.comments = comments;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public List<Comentario> getComments() { return comments; }
    public void setComments(List<Comentario> comments) { this.comments = comments; }
}
