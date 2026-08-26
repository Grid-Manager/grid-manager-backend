package com.Lino.grid_manager_back.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Lino.grid_manager_back.category.dto.CategoryResponse;
import com.Lino.grid_manager_back.category.dto.CategoryScoringProfileResponse;
import com.Lino.grid_manager_back.category.dto.CreateCategoryRequest;
import com.Lino.grid_manager_back.category.dto.CreateCategoryScoringProfileRequest;
import com.Lino.grid_manager_back.category.dto.UpdateCategoryRequest;
import com.Lino.grid_manager_back.category.service.CategoryService;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }

    @Operation(summary = "Cria uma categoria", description = "Cria uma categoria com nome e sigla exclusivos.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Categoria criada"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "409", description = "Nome ou sigla j\u00e1 existente")})
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Consulta uma categoria")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Categoria encontrada"), @ApiResponse(responseCode = "404", description = "Categoria n\u00e3o encontrada")})
    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "Lista categorias", description = "Retorna categorias paginadas.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Lista paginada"))
    @GetMapping
    public PagedResponse<CategoryResponse> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return service.findAll(pageable);
    }

    @Operation(summary = "Atualiza uma categoria")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Categoria atualizada"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "404", description = "Categoria n\u00e3o encontrada")})
    @PatchMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Remove uma categoria")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Categoria removida"), @ApiResponse(responseCode = "404", description = "Categoria n\u00e3o encontrada"), @ApiResponse(responseCode = "409", description = "Categoria ainda vinculada")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Configura a pontua\u00e7\u00e3o de uma categoria", description = "Define regras por tipo de corrida e data de vig\u00eancia.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Perfil criado"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "409", description = "Perfil duplicado")})
    @PostMapping("/{categoryId}/scoring-profiles")
    public ResponseEntity<CategoryScoringProfileResponse> createScoringProfile(@PathVariable Long categoryId,
            @Valid @RequestBody CreateCategoryScoringProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createScoringProfile(categoryId, request));
    }
}
