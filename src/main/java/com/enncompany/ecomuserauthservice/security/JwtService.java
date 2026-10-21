package com.enncompany.ecomuserauthservice.security;

import com.enncompany.ecomuserauthservice.config.JwtProperties;
import com.enncompany.ecomuserauthservice.dto.AuthDtos.TokenInfo;
import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final JwtProperties properties;
  private final SecretKey key;

  public JwtService(JwtProperties properties) {
    this.properties = properties;
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
  }

  public Instant expiryFor(Instant issuedAt) {
    return issuedAt.plus(properties.expiration());
  }

  public String generate(UserInfoDto user, Instant issuedAt, Instant expiresAt) {
    return Jwts.builder()
        .issuer(properties.issuer())
        .subject(user.loginId())
        .claim("userId", user.id())
        .claim("userType", user.userType())
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(expiresAt))
        .signWith(key)
        .compact();
  }

  public Optional<TokenInfo> validate(String token) {
    try {
      Claims claims =
          Jwts.parser()
              .verifyWith(key)
              .requireIssuer(properties.issuer())
              .build()
              .parseSignedClaims(token)
              .getPayload();
      return Optional.of(
          new TokenInfo(
              claims.get("userId", Long.class),
              claims.getSubject(),
              claims.get("userType", Integer.class),
              claims.getExpiration().toInstant()));
    } catch (JwtException | IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
