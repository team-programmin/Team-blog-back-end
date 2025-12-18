package com.blog.eu.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Entidade que representa um comentário associado a um {@link Post}.
 *
 * Cada comentário possui um autor, texto, data de criação e pode ser marcado
 * como modificado. Além disso, suporta estrutura hierárquica, permitindo
 * comentários em resposta a outros comentários (campo parent).
 *
 * Relacionamentos:
 * - Muitos comentários pertencem a um único {@link User} (autor)
 * - Muitos comentários pertencem a um único {@link Post}
 * - Um comentário pode ter um comentário pai (parent)
 * - Um comentário pode ter várias respostas (lista de Comentario)
 *
 * Campos principais:
 * - id: identificador único do comentário
 * - texto: conteúdo textual do comentário (máx. 500 caracteres)
 * - autor: usuário que escreveu o comentário
 * - post: post ao qual o comentário está vinculado
 * - dataCriacao: data em que o comentário foi criado
 * - modificado: indica se o comentário foi editado após a criação
 * - parent: comentário pai, caso seja uma resposta
 * - respostas: lista de respostas associadas a este comentário
 *
 * @author Luis
 */
@Entity
@Table(name = "comentarios")
public class Comentario {

    /** Identificador único do comentário */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Texto do comentário (máximo 500 caracteres, obrigatório) */
    @Column(nullable=false, length=500)
    private String texto;

    /** Usuário autor do comentário (obrigatório) */
    @ManyToOne
    @JoinColumn(name="autor_id", nullable=false)
    private User autor;

    /** Post ao qual o comentário está vinculado (obrigatório) */
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference("post-comments")
    private Post post;


    /** Data de criação do comentário */
    private String dataCriacao;

    /** Indica se o comentário foi modificado após a criação */
    private Boolean modificado;

    /** Comentário pai, caso este seja uma resposta */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference("comment-replies")
    private Comentario parent;
    @OneToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;
    /** Lista de respostas associadas a este comentário */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("comment-replies")
    private List<Comentario> respostas = new ArrayList<>();



    public Comentario(Long id, String texto, User autor, Post post, String dataCriacao, Boolean modificado,
                      Comentario parent, List<Comentario> respostas) {
        this.id = id;
        this.texto = texto;
        this.autor = autor;
        this.post = post;
        this.dataCriacao = dataCriacao;
        this.modificado = modificado;
        this.parent = parent;
        this.respostas = respostas;
    }

    public Comentario() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public User getAutor() { return autor; }
    public void setAutor(User autor) { this.autor = autor; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }

    public Boolean getModificado() { return modificado; }
    public void setModificado(Boolean modificado) { this.modificado = modificado; }

    public Comentario getParent() { return parent; }
    public void setParent(Comentario parent) { this.parent = parent; }

    public List<Comentario> getRespostas() { return respostas; }
    public void setRespostas(List<Comentario> respostas) { this.respostas = respostas; }
}
