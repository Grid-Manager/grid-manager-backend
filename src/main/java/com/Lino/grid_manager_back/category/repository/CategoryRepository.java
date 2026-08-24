package com.Lino.grid_manager_back.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Lino.grid_manager_back.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByAcronymIgnoreCase(String acronym);
}
