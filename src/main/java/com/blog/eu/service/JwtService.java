package com.blog.eu.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
   
    private final Key key;
    private final long expiresIn;

    

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expires-in}") long expiresIn) {
        // O segredo deve estar em Base64 no application.yml
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiresIn = expiresIn * 1000L; // segundos -> milissegundos
    }

    /** Gera um token JWT com o ID do usuário como subject */
    public String generateToken(Long userId, String role) {
    return Jwts.builder()
            .setSubject(userId.toString())
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiresIn))
            .signWith(key)
            .compact();
}


    /** Extrai o subject (ID do usuário) de um token JWT */
    public Long parseSubject(String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }
     public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String parseRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
