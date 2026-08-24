package com.Lino.grid_manager_back.auth.service;

import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.auth.dto.AuthResponse;
import com.Lino.grid_manager_back.auth.dto.LoginRequest;
import com.Lino.grid_manager_back.auth.dto.RegisterRequest;
import com.Lino.grid_manager_back.auth.entity.UserAccount;
import com.Lino.grid_manager_back.auth.entity.UserRole;
import com.Lino.grid_manager_back.auth.repository.UserAccountRepository;
import com.Lino.grid_manager_back.infrastructure.exception.DuplicateResourceException;
import com.Lino.grid_manager_back.infrastructure.security.JwtService;

@Service
public class AuthService {
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository repository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("E-mail j\u00e1 cadastrado.");
        }
        UserAccount account = UserAccount.builder().email(email).passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.USER).createdAt(LocalDateTime.now()).build();
        UserAccount saved = repository.save(account);
        UserDetails principal = userDetails(saved);
        return tokenResponse(principal);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(normalizeEmail(request.email()), request.password()));
        return tokenResponse((UserDetails) authentication.getPrincipal());
    }

    private AuthResponse tokenResponse(UserDetails principal) {
        return new AuthResponse(jwtService.generateToken(principal), "Bearer", jwtService.expirationInSeconds());
    }

    private UserDetails userDetails(UserAccount account) {
        return org.springframework.security.core.userdetails.User.withUsername(account.getEmail())
                .password(account.getPasswordHash()).roles(account.getRole().name()).build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
