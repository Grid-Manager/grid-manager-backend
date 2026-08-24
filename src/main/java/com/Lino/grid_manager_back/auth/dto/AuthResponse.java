package com.Lino.grid_manager_back.auth.dto;

public record AuthResponse(String accessToken, String tokenType, Long expiresInSeconds) {}
