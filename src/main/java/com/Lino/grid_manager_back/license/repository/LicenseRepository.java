package com.Lino.grid_manager_back.license.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Lino.grid_manager_back.license.entity.License;

public interface LicenseRepository extends JpaRepository<License, Long> {
    boolean existsByLicenseNumber(String licenseNumber);
}
