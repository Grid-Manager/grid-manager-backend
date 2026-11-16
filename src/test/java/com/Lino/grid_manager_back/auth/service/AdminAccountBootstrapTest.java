package com.Lino.grid_manager_back.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.Lino.grid_manager_back.auth.entity.UserAccount;
import com.Lino.grid_manager_back.auth.entity.UserRole;
import com.Lino.grid_manager_back.auth.repository.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
class AdminAccountBootstrapTest {
    @Mock
    private UserAccountRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void createsConfiguredAdminWithEncodedPassword() throws Exception {
        AdminAccountBootstrap bootstrap = new AdminAccountBootstrap(repository, passwordEncoder,
                " Admin@GridManager.com ", "password-123");
        when(repository.findByEmailIgnoreCase("admin@gridmanager.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password-123")).thenReturn("encoded-password");

        bootstrap.run(new DefaultApplicationArguments());

        ArgumentCaptor<UserAccount> account = ArgumentCaptor.forClass(UserAccount.class);
        verify(repository).save(account.capture());
        assertEquals("admin@gridmanager.com", account.getValue().getEmail());
        assertEquals("encoded-password", account.getValue().getPasswordHash());
        assertEquals(UserRole.ADMIN, account.getValue().getRole());
    }

    @Test
    void doesNotPromoteAnExistingUser() {
        AdminAccountBootstrap bootstrap = new AdminAccountBootstrap(repository, passwordEncoder,
                "user@gridmanager.com", "password-123");
        when(repository.findByEmailIgnoreCase("user@gridmanager.com")).thenReturn(Optional.of(UserAccount.builder()
                .id(1L)
                .email("user@gridmanager.com")
                .passwordHash("existing-hash")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build()));

        assertThrows(IllegalStateException.class, () -> bootstrap.run(new DefaultApplicationArguments()));
        verify(repository, never()).save(any(UserAccount.class));
    }

    @Test
    void requiresAdminEmailAndPasswordTogether() {
        AdminAccountBootstrap bootstrap = new AdminAccountBootstrap(repository, passwordEncoder,
                "admin@gridmanager.com", " ");

        assertThrows(IllegalStateException.class, () -> bootstrap.run(new DefaultApplicationArguments()));
        verify(repository, never()).findByEmailIgnoreCase(any());
    }
}
