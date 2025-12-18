package com.blog.eu.model;

import jakarta.persistence.*;

/**
 * Entidade JPA que representa uma foto associada a um {@link User}.
 *
 * Cada foto possui uma URL única, uma chave identificadora e pode ser marcada
 * como avatar do usuário. Essa entidade é utilizada para armazenar e gerenciar
 * imagens de perfil ou outras fotos vinculadas ao usuário.
 *
 * Relacionamentos:
 * - Muitas fotos podem pertencer a um único {@link User}
 *
 * Campos principais:
 * - id: identificador único da foto
 * - user: usuário ao qual a foto está vinculada
 * - url: endereço da foto armazenada
 * - keey: chave única que identifica a foto
 * - isAvatar: indica se a foto é utilizada como avatar do usuário
 *
 * @author Luis
 */
@Entity
@Table(name = "photos")
public class Photo {

    /** Identificador único da foto */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuário ao qual a foto está vinculada */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** URL da foto armazenada (obrigatória) */
    @Column(nullable = false)
    private String url;

    /** Chave única que identifica a foto (obrigatória) */
    @Column(nullable = false, unique = true)
    private String keey;

    /** Indica se a foto é utilizada como avatar do usuário */
    @Column(nullable = false)
    private boolean isAvatar = false;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getKey() { return keey; }
    public void setKey(String key) { this.keey = key; }

    public boolean isAvatar() { return isAvatar; }
    public void setAvatar(boolean isAvatar) { this.isAvatar = isAvatar; }
}
