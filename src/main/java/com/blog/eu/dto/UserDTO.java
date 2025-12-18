package com.blog.eu.dto;

import com.blog.eu.model.Role;

/**
 * DTO para representar dados públicos de um usuário.
 *
 * Inclui apenas informações necessárias para exibição,
 * sem expor campos sensíveis como senha ou relacionamentos complexos.
 */
public record UserDTO(
    Long id,
    String displayName,
    String email,
    String avatarUrl,
    Role role,
    String bio,
    String location,
    String website
) {}
