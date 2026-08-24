package com.Lino.grid_manager_back.result.controller;

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
import com.Lino.grid_manager_back.result.dto.CreateResultRequest;
import com.Lino.grid_manager_back.result.dto.ResultResponse;
import com.Lino.grid_manager_back.result.service.ResultService;

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
}
