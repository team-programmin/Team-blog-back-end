package com.blog.eu.model;

import com.blog.eu.pedidos.model.Peditos;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;


/**
 * Entidade JPA que representa um usuário do sistema.
 *
 * Cada usuário possui informações de autenticação (email e senha),
 * dados de perfil (nome de exibição, biografia, localização, site, avatar)
 * e um papel definido pela enumeração {@link Role}.
 *
 * Relacionamentos:
 * - Um usuário pode ter várias fotos associadas ({@link Photo})
 * - Um usuário pode ser autor de posts ({@link Post})
 * - Um usuário pode ser autor de comentários ({@link Comentario})
 *
 * Campos principais:
 * - id: identificador único do usuário
 * - email: endereço de e-mail único e obrigatório
 * - passwordHash: senha armazenada de forma segura (hash)
 * - displayName: nome de exibição público do usuário
 * - role: papel do usuário (USER por padrão, ou ADMIN)
 * - bio: breve descrição ou biografia (máx. 280 caracteres)
 * - location: localização informada pelo usuário
 * - website: site pessoal ou profissional
 * - avatarUrl: URL da imagem de avatar
 * - photos: lista de fotos associadas ao usuário
 *
 * Essa entidade é fundamental para autenticação, autorização e
 * gerenciamento de perfis dentro da aplicação.
 *
 * @author Luis
 */
@Entity
@Table(name = "users")
public class User {

    /** Identificador único do usuário */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Endereço de e-mail único e obrigatório */
    @Column(unique = true, nullable = false)
    private String email;

    /** Senha armazenada em formato hash (obrigatória) */
    @Column(nullable = false)
    private String passwordHash;

    /** Nome de exibição público do usuário (obrigatório) */
    @Column(nullable = false)
    private String displayName;

    /** Papel do usuário (USER por padrão) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /** Biografia do usuário (máximo 280 caracteres) */
    @Column(length = 280)
    private String bio;

    /** Localização informada pelo usuário */
    private String location;

    /** Site pessoal ou profissional */
    private String website;
   @OneToOne(mappedBy = "userQuePediu")
   @JsonBackReference
    private Peditos pedito;

    /** URL da imagem de avatar do usuário */
    private String avatarUrl;

    /** Foto associada ao usuário */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    private Photo photos;


    // Getters e Setters
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Peditos getPedito() { return pedito; }
    public void setPedito(Peditos pedito) { this.pedito = pedito; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Photo getPhotos() { return photos; }
    public void setPhotos(Photo photos) { this.photos = photos; }
}
