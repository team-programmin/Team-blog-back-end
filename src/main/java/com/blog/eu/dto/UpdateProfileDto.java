package com.blog.eu.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO utilizado para atualização de informações do perfil do usuário.
 *
 * Este objeto é usado em requisições de atualização de perfil (ex.: PUT /api/profile/me),
 * permitindo modificar dados como nome de exibição, biografia, localização e site pessoal.
 *
 * Validações aplicadas:
 * - displayName: deve ter entre 2 e 50 caracteres
 * - bio: deve ter no máximo 280 caracteres
 * - location: deve ter no máximo 80 caracteres
 * - website: campo opcional sem restrições de tamanho
 *
 * Campos principais:
 * - displayName: nome de exibição público do usuário
 * - bio: breve descrição ou biografia do usuário
 * - location: localização informada pelo usuário
 * - website: endereço de site pessoal ou profissional
 *
 * @author Luis
 */
public class UpdateProfileDto {

    /** Nome de exibição público do usuário (mínimo 2, máximo 50 caracteres) */
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    private String displayName;

    /** Biografia do usuário (máximo 280 caracteres) */
    @Size(max = 280, message = "A bio deve ter no máximo 280 caracteres")
    private String bio;

    /** Localização informada pelo usuário (máximo 80 caracteres) */
    @Size(max = 80, message = "A localização deve ter no máximo 80 caracteres")
    private String location;

    /** Site pessoal ou profissional do usuário */
    private String website;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    // getters e setters
}
