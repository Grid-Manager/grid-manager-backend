package com.Lino.grid_manager_back.auth.repository;

import com.Lino.grid_manager_back.auth.entity.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);
}
