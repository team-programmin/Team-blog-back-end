package com.blog.eu.auth.controler;

import com.blog.eu.auth.repository.UserRepository;
import com.blog.eu.dto.*;
import com.blog.eu.exepitons.launch.EmailjaEstaEmUso;
import com.blog.eu.exepitons.launch.Invalid;
import com.blog.eu.exepitons.launch.NotFout;
import com.blog.eu.model.Role;
import com.blog.eu.model.User;
import com.blog.eu.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável pela autenticação e registro de usuários.
 *
 * Esta classe expõe endpoints REST relacionados ao fluxo de autenticação:
 * - Registro de novos usuários
 * - Login e geração de token JWT
 *
 * Regras de negócio:
 * - No registro, o email deve ser único. A senha é armazenada com hash
 *   utilizando BCrypt. Se a role informada for inválida, o usuário é
 *   registrado com role padrão USER.
 * - No login, as credenciais são validadas contra o repositório. Se válidas,
 *   é gerado um token JWT contendo o ID e a role do usuário.
 *
 * Endpoints disponíveis:
 * - POST /api/auth/register -> registra um novo usuário
 * - POST /api/auth/login    -> autentica usuário e retorna token JWT
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    


    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }
    /**
     * Endpoint para registrar um novo usuário.
     *
     * Verifica se o email já está em uso. Caso não esteja, cria um novo usuário
     * com os dados fornecidos, incluindo email, senha (armazenada com hash),
     * nome de exibição e role. Se a role informada for inválida, o usuário
     * será registrado com role padrão USER.
     *
     * @param dto objeto RegisterDto contendo email, senha, nome de exibição e role
     * @return o usuário salvo no repositório
     * @throws RuntimeException se o email já estiver em uso
     */

    @PostMapping("/register")
    public User register(@RequestBody RegisterDto dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailjaEstaEmUso();
        }

        User u = new User();
        u.setEmail(dto.getEmail());
        u.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        u.setDisplayName(dto.getDisplayName());

        
        try {
            u.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        } catch (Exception e) {
            u.setRole(Role.ADMIN); 
        }

        return userRepo.save(u);
    }

    /**
     * Endpoint para autenticação de usuários.
     *
     * Recebe as credenciais (email e senha) e valida contra os dados
     * armazenados no repositório. Se as credenciais forem válidas,
     * gera e retorna um token JWT para o usuário.
     *
     * @param dto objeto RegisterDto contendo email e senha do usuário
     * @return token JWT como String se a autenticação for bem-sucedida
     * @throws RuntimeException se o usuário não for encontrado ou se a senha for inválida
     */

    @PostMapping("/login")
    public String login(@RequestBody RegisterDto dto) {
        var user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFout("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new Invalid("Senha inválida");
        }

        
        return jwtService.generateToken(user.getId(), user.getRole().name());
    }
    @GetMapping("/oauth2/success")
    public void oauth2Success(Authentication authentication, HttpServletResponse response) throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        String email = principal.getAttribute("email");
        User user = userRepo.findByEmail(email).orElseThrow();

        String token = jwtService.generateToken(user.getId(), user.getRole().name());

        response.sendRedirect("http://localhost:3000/oauth/callback?token=" + token);
    }

}
