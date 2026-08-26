package com.Lino.grid_manager_back.season.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Lino.grid_manager_back.season.dto.CreateSeasonRequest;
import com.Lino.grid_manager_back.season.dto.SeasonResponse;
import com.Lino.grid_manager_back.season.dto.UpdateSeasonRequest;
import com.Lino.grid_manager_back.season.service.SeasonService;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

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

    @Operation(summary = "Finaliza uma temporada", description = "Define o vencedor pela maior pontua\u00e7\u00e3o ap\u00f3s a finaliza\u00e7\u00e3o de todas as corridas.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Temporada finalizada"), @ApiResponse(responseCode = "400", description = "Corridas pendentes, empate ou resultados ausentes"), @ApiResponse(responseCode = "404", description = "Temporada ou piloto n\u00e3o encontrado")})
    @PatchMapping("/{id}/finish")
    public SeasonResponse finish(@PathVariable Long id) { return service.finish(id); }

    @Operation(summary = "Lista temporadas", description = "Retorna temporadas paginadas.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Lista paginada"))
    @GetMapping
    public PagedResponse<SeasonResponse> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return service.findAll(pageable);
    }

    @Operation(summary = "Atualiza uma temporada")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Temporada atualizada"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "404", description = "Temporada não encontrada")})
    @PatchMapping("/{id}")
    public SeasonResponse update(@PathVariable Long id, @Valid @RequestBody UpdateSeasonRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Remove uma temporada")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Temporada removida"), @ApiResponse(responseCode = "404", description = "Temporada não encontrada"), @ApiResponse(responseCode = "409", description = "Temporada ainda vinculada a corridas")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
