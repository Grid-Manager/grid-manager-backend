package com.Lino.grid_manager_back.result.controller;

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
import com.Lino.grid_manager_back.result.dto.CreateResultRequest;
import com.Lino.grid_manager_back.result.dto.ResultResponse;
import com.Lino.grid_manager_back.result.dto.UpdateResultRequest;
import com.Lino.grid_manager_back.result.service.ResultService;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

@RestController
@RequestMapping("/api/v1/results")
public class ResultController {
    private final ResultService service;
    public ResultController(ResultService service) { this.service = service; }

    @Operation(summary = "Registra resultado de corrida", description = "Calcula a pontua\u00e7\u00e3o conforme o perfil vigente da categoria.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Resultado registrado"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "409", description = "Resultado ou posi\u00e7\u00e3o duplicada")})
    @PostMapping
    public ResponseEntity<ResultResponse> create(@Valid @RequestBody CreateResultRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Consulta um resultado")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Resultado encontrado"), @ApiResponse(responseCode = "404", description = "Resultado n\u00e3o encontrado")})
    @GetMapping("/{id}")
    public ResultResponse findById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "Lista resultados", description = "Retorna resultados paginados.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Lista paginada"))
    @GetMapping
    public PagedResponse<ResultResponse> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return service.findAll(pageable);
    }

    @Operation(summary = "Atualiza um resultado", description = "Recalcula a pontuação após a alteração.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Resultado atualizado"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "404", description = "Resultado não encontrado"), @ApiResponse(responseCode = "409", description = "Posição final duplicada")})
    @PatchMapping("/{id}")
    public ResultResponse update(@PathVariable Long id, @Valid @RequestBody UpdateResultRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Remove um resultado")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Resultado removido"), @ApiResponse(responseCode = "404", description = "Resultado não encontrado")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
