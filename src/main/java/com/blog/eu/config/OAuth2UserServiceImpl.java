package com.blog.eu.config;


import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.blog.eu.auth.repository.UserRepository;
import com.blog.eu.model.User;
import com.blog.eu.model.Role;

@Service
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserRepository userRepo;

    public OAuth2UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauthUser = delegate.loadUser(userRequest);
        Map<String, Object> attributes = oauthUser.getAttributes();

        // Pegar email
        String email = (String) attributes.get("email");

        // Nome
        String name = (String) attributes.get("name");

        // Foto: Google usa "picture", GitHub usa "avatar_url"
        String avatar = attributes.containsKey("picture")
                        ? (String) attributes.get("picture")
                        : (String) attributes.get("avatar_url");

        // Salvar/atualizar no banco
        User user = userRepo.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setDisplayName(name);
        user.setAvatarUrl(avatar);
        user.setRole(Role.USER);
        userRepo.save(user);

        return oauthUser;
    }
}
