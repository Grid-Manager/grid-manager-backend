package com.Lino.grid_manager_back.climate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
import com.Lino.grid_manager_back.climate.dto.ClimateResponse;
import com.Lino.grid_manager_back.climate.dto.CreateClimateRequest;
import com.Lino.grid_manager_back.climate.dto.UpdateClimateRequest;
import com.Lino.grid_manager_back.climate.service.ClimateService;

@RestController
@RequestMapping("/api/v1/climates")
public class ClimateController {
    private final ClimateService service;
    public ClimateController(ClimateService service) { this.service = service; }

    @Operation(summary = "Registra o clima de uma corrida")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Clima registrado"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "409", description = "Clima da corrida j\u00e1 registrado")})
    @PostMapping
    public ResponseEntity<ClimateResponse> create(@Valid @RequestBody CreateClimateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Consulta um registro clim\u00e1tico")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Clima encontrado"), @ApiResponse(responseCode = "404", description = "Clima n\u00e3o encontrado")})
    @GetMapping("/{id}")
    public ClimateResponse findById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "Atualiza um registro clim\u00e1tico")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Clima atualizado"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "404", description = "Clima n\u00e3o encontrado")})
    @PatchMapping("/{id}")
    public ClimateResponse update(@PathVariable Long id, @Valid @RequestBody UpdateClimateRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Remove um registro clim\u00e1tico")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Clima removido"), @ApiResponse(responseCode = "404", description = "Clima n\u00e3o encontrado")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
