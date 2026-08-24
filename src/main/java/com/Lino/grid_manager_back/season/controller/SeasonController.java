package com.Lino.grid_manager_back.season.controller;

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
import com.Lino.grid_manager_back.season.dto.CreateSeasonRequest;
import com.Lino.grid_manager_back.season.dto.SeasonResponse;
import com.Lino.grid_manager_back.season.service.SeasonService;

@RestController
@RequestMapping("/api/v1/seasons")
public class SeasonController {
    private final SeasonService service;
    public SeasonController(SeasonService service) { this.service = service; }

    @Operation(summary = "Cria uma temporada", description = "Vincula a temporada a uma categoria e, opcionalmente, a pilotos da mesma categoria.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Temporada criada"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "404", description = "Categoria ou piloto n\u00e3o encontrado")})
    @PostMapping
    public ResponseEntity<SeasonResponse> create(@Valid @RequestBody CreateSeasonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Consulta uma temporada")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Temporada encontrada"), @ApiResponse(responseCode = "404", description = "Temporada n\u00e3o encontrada")})
    @GetMapping("/{id}")
    public SeasonResponse findById(@PathVariable Long id) { return service.findById(id); }
}
