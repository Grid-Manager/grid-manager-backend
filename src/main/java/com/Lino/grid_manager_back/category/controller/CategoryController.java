package com.Lino.grid_manager_back.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Lino.grid_manager_back.category.dto.CategoryResponse;
import com.Lino.grid_manager_back.category.dto.CreateCategoryRequest;
import com.Lino.grid_manager_back.category.service.CategoryService;

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
}
