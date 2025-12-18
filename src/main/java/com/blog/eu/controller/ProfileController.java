package com.blog.eu.controller;




import jakarta.servlet.http.HttpServletRequest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blog.eu.dto.UpdateProfileDto;
import com.blog.eu.model.User;
import com.blog.eu.service.JwtService;
import com.blog.eu.service.ProfileService;

/**
 * Controlador responsável por gerenciar operações relacionadas ao perfil do usuário.
 *
 * Esta classe está mapeada para o caminho base "/api/profile" e fornece endpoints
 * que permitem ao usuário autenticado visualizar e atualizar suas informações
 * pessoais, incluindo dados de perfil e fotos.
 *
 * Funcionalidades principais:
 * - Obter informações do perfil do usuário autenticado
 * - Atualizar dados do perfil (nome de exibição, biografia, localização, site)
 * - Fazer upload de fotos de perfil ou avatar
 * - Definir uma foto existente como avatar
 * - Excluir fotos associadas ao perfil
 *
 * O acesso é validado através de tokens JWT fornecidos no cabeçalho "Authorization".
 * O método privado userId extrai o identificador do usuário a partir do token.
 *
 * @author SeuNome
 * @see ProfileService
 * @see JwtService
 * @see UpdateProfileDto
 * @see User
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtService jwtService;

    public ProfileController(ProfileService profileService, JwtService jwtService) {
        this.profileService = profileService;
        this.jwtService = jwtService;
    }

    /**
     * Obtém o ID do usuário autenticado a partir do token JWT presente no cabeçalho Authorization.
     *
     * @param req objeto HttpServletRequest contendo os detalhes da requisição
     * @return identificador único do usuário autenticado
     * @throws RuntimeException se o token estiver ausente ou inválido
     */
    private Long userId(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) throw new RuntimeException("Token ausente");
        return jwtService.parseSubject(header.substring(7));
    }

    /**
     * Endpoint GET para obter informações do perfil do usuário autenticado.
     *
     * @param req objeto HttpServletRequest usado para validar o acesso
     * @return objeto User representando o perfil do usuário
     */
    @GetMapping("/me")
    public User getMe(HttpServletRequest req) {
        return profileService.getMe(userId(req));
    }

    /**
     * Endpoint PUT para atualizar informações do perfil do usuário autenticado.
     *
     * @param req objeto HttpServletRequest usado para validar o acesso
     * @param dto objeto UpdateProfileDto contendo os novos dados do perfil
     * @return objeto User atualizado
     */
    @PutMapping("/me")
    public User updateMe(HttpServletRequest req, @Validated @RequestBody UpdateProfileDto dto) {
        return profileService.updateMe(userId(req), dto.getDisplayName(), dto.getBio(), dto.getLocation(), dto.getWebsite());
    }

    /**
     * Endpoint POST para fazer upload de uma foto de perfil ou avatar.
     *
     * @param req objeto HttpServletRequest usado para validar o acesso
     * @param photo arquivo MultipartFile representando a foto enviada
     * @param isAvatar indica se a foto enviada deve ser definida como avatar
     * @return objeto User atualizado com a nova foto
     * @throws Exception se ocorrer erro no upload
     */
    @PostMapping("/me/photo")
    public User uploadPhoto(HttpServletRequest req,
                            @RequestParam("photo") MultipartFile photo,
                            @RequestParam(value = "isAvatar", defaultValue = "false") boolean isAvatar) throws Exception {
        return profileService.uploadPhoto(userId(req), photo, isAvatar);
    }

    /**
     * Endpoint POST para definir uma foto existente como avatar do usuário.
     *
     * @param req objeto HttpServletRequest usado para validar o acesso
     * @param key chave identificadora da foto armazenada
     * @return objeto User atualizado com o novo avatar
     */
    @PostMapping("/me/avatar")
    public User setAvatar(HttpServletRequest req, @RequestParam("key") String key) {
        return profileService.setAvatar(userId(req), key);
    }

    /**
     * Endpoint DELETE para remover uma foto associada ao perfil do usuário.
     *
     * @param req objeto HttpServletRequest usado para validar o acesso
     * @param key chave identificadora da foto a ser removida
     * @return objeto User atualizado após a exclusão da foto
     */
    @DeleteMapping("/me/photo")
public User deletePhoto(HttpServletRequest req) {
    return profileService.deletePhoto(userId(req));
}

}
