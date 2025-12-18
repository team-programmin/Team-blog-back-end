package com.blog.eu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.eu.auth.repository.UserRepository;
import com.blog.eu.dto.UpdateUserRoleDTO;
import com.blog.eu.dto.UserDTO;
import com.blog.eu.infos.RequestCounter;
import com.blog.eu.model.Role;
import com.blog.eu.model.User;
import com.blog.eu.service.JwtService;


/**
 * Controlador responsável por gerenciar endpoints administrativos da aplicação.
 *
 * Esta classe está mapeada para o caminho base "/api/admin" e contém operações
 * restritas a usuários com papel {@link Role#ADMIN}. O acesso é validado
 * através de tokens JWT fornecidos no cabeçalho "Authorization".
 *
 * Funcionalidades principais:
 * - Listar todos os usuários cadastrados
 * - Adicionar novos usuários com papel de administrador
 * - Obter informações detalhadas de um usuário pelo seu ID
 * - Verificar se um usuário possui papel ADMIN
 * - Consultar o número de requisições realizadas no dia
 *
 * O controle de acesso é feito pelo método verificarAcesso, que valida
 * o token JWT e garante que apenas administradores possam acessar os recursos.
 *
 * @author SeuNome
 * @see UserRepository
 * @see JwtService
 * @see RequestCounter
 */

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RequestCounter requestCounter;

    public AdminController(UserRepository userRepository, RequestCounter requestCounter) {
        this.userRepository = userRepository;
        this.requestCounter = requestCounter;
    }



    @PutMapping("/update/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setDisplayName(user.getDisplayName());
            existingUser.setEmail(user.getEmail());
            existingUser.setAvatarUrl(user.getAvatarUrl());
            existingUser.setBio(user.getBio());
            existingUser.setLocation(user.getLocation());
            existingUser.setWebsite(user.getWebsite());
            return ResponseEntity.ok().body(userRepository.save(existingUser));
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getDisplayName(),
                        user.getEmail(),
                        user.getAvatarUrl(),
                        user.getRole(),
                        user.getBio(),
                        user.getLocation(),
                        user.getWebsite()
                ))
                .toList();
    }

    @PostMapping("/add/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public User addAdminUser(@RequestBody User user) {
        user.setRole(Role.ADMIN);
        return userRepository.save(user);
    }

    @GetMapping("/info/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserInfo(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @GetMapping("/lock/getuseradmin")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean getUserAdmin(@RequestBody User user) {
        return user.getRole() == Role.ADMIN;
    }

    @DeleteMapping("/delete/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    
    @GetMapping("/info/requisitions")
    @PreAuthorize("hasRole('ADMIN')")
    public int getRequisitions() {
        return requestCounter.getCount();
    }

    @PutMapping("/up/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(@RequestBody UpdateUserRoleDTO dto) {

        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setRole(dto.getRole());

        return ResponseEntity.ok(userRepository.save(user));
    }

}
