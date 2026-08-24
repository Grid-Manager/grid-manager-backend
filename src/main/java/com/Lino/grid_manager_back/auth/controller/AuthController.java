package com.Lino.grid_manager_back.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Lino.grid_manager_back.auth.dto.AuthResponse;
import com.Lino.grid_manager_back.auth.dto.LoginRequest;
import com.Lino.grid_manager_back.auth.dto.RegisterRequest;
import com.Lino.grid_manager_back.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Registra uma conta", description = "Cria uma conta com senha protegida por BCrypt e retorna um JWT.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Conta criada"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "409", description = "E-mail j\u00e1 cadastrado")})
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    @Operation(summary = "Autentica uma conta", description = "Retorna um JWT Bearer para chamadas autenticadas.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Autenticado"), @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos"), @ApiResponse(responseCode = "401", description = "Credenciais inv\u00e1lidas")})
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }
}
