package com.Lino.grid_manager_back.category.service;

import java.time.Year;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.category.dto.CategoryResponse;
import com.Lino.grid_manager_back.category.dto.CategoryScoringProfileResponse;
import com.Lino.grid_manager_back.category.dto.CreateCategoryRequest;
import com.Lino.grid_manager_back.category.dto.CreateCategoryScoringProfileRequest;
import com.Lino.grid_manager_back.category.dto.PositionPointsRequest;
import com.Lino.grid_manager_back.category.dto.UpdateCategoryRequest;
import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.category.entity.CategoryScoringProfile;
import com.Lino.grid_manager_back.category.entity.CategoryScoringRule;
import com.Lino.grid_manager_back.category.mapper.CategoryMapper;
import com.Lino.grid_manager_back.category.repository.CategoryScoringProfileRepository;
import com.Lino.grid_manager_back.category.repository.CategoryRepository;
import com.Lino.grid_manager_back.infrastructure.exception.DuplicateResourceException;
import com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryScoringProfileRepository scoringProfileRepository;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository repository, CategoryScoringProfileRepository scoringProfileRepository,
            CategoryMapper mapper) {
        this.repository = repository;
        this.scoringProfileRepository = scoringProfileRepository;
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
        return mapper.toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<CategoryResponse> findAll(Pageable pageable) {
        Page<CategoryResponse> page = repository.findAll(pageable).map(mapper::toResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = findEntity(id);
        if (request.name() != null) {
            String name = request.name().trim();
            if (repository.existsByNameIgnoreCase(name) && !category.getName().equalsIgnoreCase(name)) {
                throw new DuplicateResourceException("Nome de categoria j\u00e1 cadastrado.");
            }
        }
        if (request.foundingYear() != null && request.foundingYear() > Year.now().getValue()) {
            throw new IllegalArgumentException("Ano de funda\u00e7\u00e3o n\u00e3o pode estar no futuro.");
        }
        mapper.update(request, category);
        if (request.name() != null) {
            category.setName(request.name().trim());
        }
        return mapper.toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findEntity(id));
    }

    @Transactional
    public CategoryScoringProfileResponse createScoringProfile(
            Long categoryId, CreateCategoryScoringProfileRequest request) {
        Category category = findEntity(categoryId);
        if (scoringProfileRepository.existsByCategoryIdAndRaceTypeAndEffectiveFrom(
                categoryId, request.raceType(), request.effectiveFrom())) {
            throw new DuplicateResourceException("J\u00e1 existe perfil de pontua\u00e7\u00e3o para esta data e tipo de corrida.");
        }
        Set<Integer> positions = new HashSet<>();
        if (!request.positionRules().stream().allMatch(rule -> positions.add(rule.position()))) {
            throw new IllegalArgumentException("Cada posi\u00e7\u00e3o deve aparecer apenas uma vez no perfil.");
        }
        CategoryScoringProfile profile = CategoryScoringProfile.builder()
                .category(category)
                .raceType(request.raceType())
                .effectiveFrom(request.effectiveFrom())
                .fastestLapBonus(request.fastestLapBonus())
                .fastestLapRequiresTopTen(request.fastestLapRequiresTopTen())
                .positionRules(new HashSet<>())
                .build();
        request.positionRules().forEach(rule -> profile.getPositionRules().add(CategoryScoringRule.builder()
                .profile(profile).finishingPosition(rule.position()).points(rule.points()).build()));
        return toScoringProfileResponse(scoringProfileRepository.save(profile));
    }

    private CategoryScoringProfileResponse toScoringProfileResponse(CategoryScoringProfile profile) {
        Set<PositionPointsRequest> rules = profile.getPositionRules().stream()
                .map(rule -> new PositionPointsRequest(rule.getFinishingPosition(), rule.getPoints()))
                .collect(java.util.stream.Collectors.toSet());
        return new CategoryScoringProfileResponse(profile.getId(), profile.getCategory().getId(), profile.getRaceType(),
                profile.getEffectiveFrom(), profile.getFastestLapBonus(), profile.isFastestLapRequiresTopTen(), rules);
    }

    private Category findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria n\u00e3o encontrada."));
    }
}
