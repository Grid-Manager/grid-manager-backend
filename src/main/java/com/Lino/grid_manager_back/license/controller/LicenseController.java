package com.Lino.grid_manager_back.license.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Lino.grid_manager_back.license.dto.LicenseResponse;
import com.Lino.grid_manager_back.license.service.LicenseService;

@RestController
@RequestMapping("/api/v1/licenses")
public class LicenseController {
    private final LicenseService service;
    public LicenseController(LicenseService service) { this.service = service; }

    @Operation(summary = "Consulta uma licen\u00e7a", description = "Licen\u00e7as s\u00e3o criadas apenas junto com o piloto.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Licen\u00e7a encontrada"), @ApiResponse(responseCode = "404", description = "Licen\u00e7a n\u00e3o encontrada")})
    @GetMapping("/{id}")
    public LicenseResponse findById(@PathVariable Long id) { return service.findById(id); }
}
