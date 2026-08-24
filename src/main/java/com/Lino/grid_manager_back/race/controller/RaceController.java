package com.Lino.grid_manager_back.race.controller;

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
import com.Lino.grid_manager_back.race.dto.CreateRaceRequest;
import com.Lino.grid_manager_back.race.dto.RaceResponse;
import com.Lino.grid_manager_back.race.service.RaceService;

@RestController
@RequestMapping("/api/v1/races")
public class RaceController {
    private final RaceService service;
    public RaceController(RaceService service) { this.service = service; }

    @Operation(summary = "Cria uma corrida", description = "Cria uma corrida agendada vinculada a uma temporada existente.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Corrida criada"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "404", description = "Temporada n\u00e3o encontrada")})
    @PostMapping
    public ResponseEntity<RaceResponse> create(@Valid @RequestBody CreateRaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Consulta uma corrida")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Corrida encontrada"), @ApiResponse(responseCode = "404", description = "Corrida n\u00e3o encontrada")})
    @GetMapping("/{id}")
    public RaceResponse findById(@PathVariable Long id) { return service.findById(id); }
}
