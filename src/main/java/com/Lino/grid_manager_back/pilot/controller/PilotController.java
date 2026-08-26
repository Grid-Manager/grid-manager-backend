package com.Lino.grid_manager_back.pilot.controller;

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
import com.Lino.grid_manager_back.pilot.dto.CreatePilotRequest;
import com.Lino.grid_manager_back.pilot.dto.PilotResponse;
import com.Lino.grid_manager_back.pilot.dto.UpdatePilotRequest;
import com.Lino.grid_manager_back.pilot.service.PilotService;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

@RestController
@RequestMapping("/api/v1/pilots")
public class PilotController {
    private final PilotService service;
    public PilotController(PilotService service) { this.service = service; }

    @Operation(summary = "Cria um piloto", description = "Valida categoria, licen\u00e7a, idade e nome do piloto.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Piloto criado"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "409", description = "Dados exclusivos em uso")})
    @PostMapping
    public ResponseEntity<PilotResponse> create(@Valid @RequestBody CreatePilotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Consulta um piloto")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Piloto encontrado"), @ApiResponse(responseCode = "404", description = "Piloto n\u00e3o encontrado")})
    @GetMapping("/{id}")
    public PilotResponse findById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "Lista pilotos", description = "Retorna pilotos paginados.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Lista paginada"))
    @GetMapping
    public PagedResponse<PilotResponse> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return service.findAll(pageable);
    }

    @Operation(summary = "Atualiza um piloto")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Piloto atualizado"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "404", description = "Piloto n\u00e3o encontrado")})
    @PatchMapping("/{id}")
    public PilotResponse update(@PathVariable Long id, @Valid @RequestBody UpdatePilotRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Remove um piloto")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Piloto removido"), @ApiResponse(responseCode = "404", description = "Piloto n\u00e3o encontrado"), @ApiResponse(responseCode = "409", description = "Piloto ainda vinculado")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
