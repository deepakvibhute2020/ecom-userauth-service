package com.enncompany.ecomuserauthservice.dto;

import java.time.Instant;

public final class AuthDtos {

  private AuthDtos() {}

  public record LoginRequest(String loginId, String password) {}

  public record LoginResponse(String token, String tokenType, Instant expiresAt, UserInfoDto user) {}

  public record TokenInfo(Long userId, String loginId, Integer userType, Instant expiresAt) {}

  public record ErrorResponse(String message) {}
}
