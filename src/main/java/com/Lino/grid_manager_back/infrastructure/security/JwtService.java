package com.Lino.grid_manager_back.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final String secret;
    private final Duration expiration;

    public JwtService(@Value("${grid-manager.jwt.secret:}") String secret,
            @Value("${grid-manager.jwt.expiration:PT2H}") Duration expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getUsername()).issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration))).signWith(signingKey()).compact();
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        return user.getUsername().equalsIgnoreCase(extractUsername(token))
                && claims(token).getExpiration().after(new Date());
    }

    public long expirationInSeconds() {
        return expiration.toSeconds();
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey signingKey() {
        if (secret.isBlank()) {
            throw new IllegalStateException("A vari\u00e1vel GRID_MANAGER_JWT_SECRET deve ser configurada.");
        }
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32) {
            throw new IllegalStateException("GRID_MANAGER_JWT_SECRET deve ter pelo menos 32 caracteres.");
        }
        return Keys.hmacShaKeyFor(key);
    }
}
