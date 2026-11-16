package com.Lino.grid_manager_back.auth.service;

import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.Lino.grid_manager_back.auth.entity.UserAccount;
import com.Lino.grid_manager_back.auth.entity.UserRole;
import com.Lino.grid_manager_back.auth.repository.UserAccountRepository;

@Component
public class AdminAccountBootstrap implements ApplicationRunner {
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String configuredEmail;
    private final String configuredPassword;

    public AdminAccountBootstrap(
            UserAccountRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${grid-manager.bootstrap-admin.email:}") String configuredEmail,
            @Value("${grid-manager.bootstrap-admin.password:}") String configuredPassword) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.configuredEmail = configuredEmail;
        this.configuredPassword = configuredPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean hasEmail = StringUtils.hasText(configuredEmail);
        boolean hasPassword = StringUtils.hasText(configuredPassword);
        if (!hasEmail && !hasPassword) {
            return;
        }
        if (!hasEmail || !hasPassword) {
            throw new IllegalStateException(
                    "Configure GRID_MANAGER_ADMIN_EMAIL e GRID_MANAGER_ADMIN_PASSWORD juntos.");
        }
        if (configuredPassword.length() < 8 || configuredPassword.length() > 72) {
            throw new IllegalStateException("GRID_MANAGER_ADMIN_PASSWORD deve ter entre 8 e 72 caracteres.");
        }

        String email = configuredEmail.trim().toLowerCase(Locale.ROOT);
        var existingAccount = repository.findByEmailIgnoreCase(email);
        if (existingAccount.isPresent()) {
            if (existingAccount.get().getRole() != UserRole.ADMIN) {
                throw new IllegalStateException(
                        "O e-mail configurado já pertence a uma conta USER; a conta não será promovida automaticamente.");
            }
            return;
        }

        repository.save(UserAccount.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(configuredPassword))
                .role(UserRole.ADMIN)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
