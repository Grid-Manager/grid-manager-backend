package com.Lino.grid_manager_back.pilot.controller;

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
import com.Lino.grid_manager_back.pilot.dto.CreatePilotRequest;
import com.Lino.grid_manager_back.pilot.dto.PilotResponse;
import com.Lino.grid_manager_back.pilot.service.PilotService;

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
}
