package com.Lino.grid_manager_back.category.service;

import java.time.Year;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.category.dto.CategoryResponse;
import com.Lino.grid_manager_back.category.dto.CreateCategoryRequest;
import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.category.mapper.CategoryMapper;
import com.Lino.grid_manager_back.category.repository.CategoryRepository;
import com.Lino.grid_manager_back.infrastructure.exception.DuplicateResourceException;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        String acronym = request.acronym().trim().toUpperCase(Locale.ROOT);
        if (repository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Nome de categoria já cadastrado.");
        }
        if (repository.existsByAcronymIgnoreCase(acronym)) {
            throw new DuplicateResourceException("Sigla de categoria já cadastrada.");
        }
        if (request.foundingYear() > Year.now().getValue()) {
            throw new IllegalArgumentException("Ano de fundação não pode estar no futuro.");
        }
        Category category = mapper.toEntity(request);
        category.setAcronym(acronym);
        return mapper.toResponse(repository.save(category));
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException(
                        "Categoria n\u00e3o encontrada.")));
    }
}
