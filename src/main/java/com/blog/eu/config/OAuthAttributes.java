package com.blog.eu.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.blog.eu.auth.repository.UserRepository;
import com.blog.eu.model.Role;
import com.blog.eu.model.User;

public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String name;
    private String email;
    private String picture;
    private String provider; // google ou github

    public OAuthAttributes(Map<String, Object> attributes, String name, String email, String picture, String provider) {
        this.attributes = attributes;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
    }

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return new OAuthAttributes(
                attributes,
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                (String) attributes.get("picture"),
                "google"
            );
        } else if ("github".equals(registrationId)) {
            // No GitHub, o atributo para avatar é "avatar_url"
            return new OAuthAttributes(
                attributes,
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                (String) attributes.get("avatar_url"),
                "github"
            );
        }
        throw new IllegalArgumentException("Login não suportado: " + registrationId);
    }

    public User toEntity() {
        User user = new User();
        user.setDisplayName(name);
        user.setEmail(email);
        user.setAvatarUrl(picture);
        user.setRole(Role.USER);
        return user;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Bean
public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(UserRepository userRepo) {
    DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    return request -> {
        OAuth2User oauthUser = delegate.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId();
        String email = oauthUser.getAttribute("email");

        User user = userRepo.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setDisplayName(oauthUser.getAttribute("name"));

        // Google usa "picture", GitHub usa "avatar_url"
        String avatar = oauthUser.getAttribute(
            registrationId.equals("github") ? "avatar_url" : "picture"
        );
        user.setAvatarUrl(avatar);

        user.setRole(Role.USER);
        userRepo.save(user);

        return oauthUser;
    };
}

}
