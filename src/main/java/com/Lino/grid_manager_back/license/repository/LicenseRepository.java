package com.Lino.grid_manager_back.license.repository;

import com.Lino.grid_manager_back.license.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<License, Long> {
  boolean existsByLicenseNumber(String licenseNumber);
}
