package com.enncompany.ecomuserauthservice.controller;

import com.enncompany.ecomuserauthservice.client.MasterDataClient;
import com.enncompany.ecomuserauthservice.dto.AuthDtos.ErrorResponse;
import com.enncompany.ecomuserauthservice.dto.AuthDtos.LoginRequest;
import com.enncompany.ecomuserauthservice.dto.AuthDtos.LoginResponse;
import com.enncompany.ecomuserauthservice.dto.AuthDtos.TokenInfo;
import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import com.enncompany.ecomuserauthservice.security.JwtAuthFilter;
import com.enncompany.ecomuserauthservice.security.JwtService;
import com.enncompany.ecomuserauthservice.security.PasswordDecryptor;
import java.time.Instant;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final MasterDataClient masterdataClient;
  private final JwtService jwtService;
  private final PasswordDecryptor passwordDecryptor;
  private final PasswordEncoder passwordEncoder;

  public AuthController(
      MasterDataClient masterdataClient,
      JwtService jwtService,
      PasswordDecryptor passwordDecryptor,
      PasswordEncoder passwordEncoder) {
    this.masterdataClient = masterdataClient;
    this.jwtService = jwtService;
    this.passwordDecryptor = passwordDecryptor;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    if (request.loginId() == null
        || request.loginId().isBlank()
        || request.password() == null
        || request.password().isBlank()) {
      return error(HttpStatus.BAD_REQUEST, "loginId and password are required");
    }
    Optional<UserInfoDto> user = masterdataClient.findByLoginId(request.loginId());
    if (user.isEmpty() || !Boolean.TRUE.equals(user.get().active())) {
      return error(HttpStatus.UNAUTHORIZED, "User not found");
    }
    // Argon2 hashes are salted, so the decrypted password is checked with matches(), not re-hashed.
    Optional<String> plain = passwordDecryptor.decrypt(request.password());
    if (plain.isEmpty()
        || user.get().userPass() == null
        || !passwordEncoder.matches(plain.get(), user.get().userPass())) {
      return error(HttpStatus.UNAUTHORIZED, "User credential does not match");
    }
    Instant now = Instant.now();
    Instant expiresAt = jwtService.expiryFor(now);
    String token = jwtService.generate(user.get(), now, expiresAt);
    return ResponseEntity.ok(new LoginResponse(token, "Bearer", expiresAt, user.get()));
  }

  private static ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(new ErrorResponse(message));
  }

  /** Other services call this with the user's Bearer token; the filter has already validated it. */
  @GetMapping("/validate")
  public TokenInfo validate(@RequestAttribute(JwtAuthFilter.TOKEN_INFO_ATTRIBUTE) TokenInfo info) {
    return info;
  }
}
