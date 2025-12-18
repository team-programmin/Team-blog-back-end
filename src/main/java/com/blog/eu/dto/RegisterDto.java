package com.blog.eu.dto;

/**
 * DTO utilizado para transferência de dados durante o processo de registro de usuários.
 *
 * Contém informações básicas necessárias para criar um novo usuário no sistema,
 * incluindo email, senha, nome de exibição e papel (role).
 *
 * Este objeto é geralmente utilizado em requisições de cadastro (ex.: POST /register).
 *
 * Campos principais:
 * - email: endereço de e-mail único do usuário
 * - password: senha escolhida pelo usuário
 * - displayName: nome de exibição público do usuário
 * - role: papel atribuído ao usuário (ex.: "USER", "ADMIN")
 *
 * @author Luis
 */
public class RegisterDto {

    /** Endereço de e-mail único do usuário */
    private String email;

    /** Senha escolhida pelo usuário */
    private String password;

    /** Nome de exibição público do usuário */
    private String displayName;

    /** Papel atribuído ao usuário (ex.: "USER", "ADMIN") */
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // getters e setters
}
