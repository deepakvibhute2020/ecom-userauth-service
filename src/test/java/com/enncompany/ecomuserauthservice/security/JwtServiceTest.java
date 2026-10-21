package com.enncompany.ecomuserauthservice.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.enncompany.ecomuserauthservice.config.JwtProperties;
import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private static final String SECRET =
      "ZWNvbS11c2VyYXV0aC1zZXJ2aWNlLWRldi1vbmx5LXNlY3JldC1rZXktMzItYnl0ZXM=";
  private static final String OTHER_SECRET =
      "b3RoZXItc2VjcmV0LWtleS1vdGhlci1zZWNyZXQta2V5LW90aGVyLXNlY3JldA==";

  private final JwtService service =
      new JwtService(new JwtProperties(SECRET, "iss", Duration.ofDays(1)));
  private final UserInfoDto user = new UserInfoDto(7L, "bob", 2, "hash", "b@x.com", true);

  @Test
  void expiryIsConfiguredDuration() {
    Instant now = Instant.parse("2026-01-01T00:00:00Z");
    assertThat(service.expiryFor(now)).isEqualTo(now.plus(Duration.ofDays(1)));
  }

  @Test
  void generatedTokenValidates() {
    Instant now = Instant.now();
    Instant exp = service.expiryFor(now);
    var info = service.validate(service.generate(user, now, exp));
    assertThat(info).isPresent();
    assertThat(info.get().userId()).isEqualTo(7L);
    assertThat(info.get().loginId()).isEqualTo("bob");
    assertThat(info.get().userType()).isEqualTo(2);
    assertThat(info.get().expiresAt().getEpochSecond()).isEqualTo(exp.getEpochSecond());
  }

  @Test
  void expiredTokenRejected() {
    Instant past = Instant.now().minusSeconds(3600);
    assertThat(service.validate(service.generate(user, past.minusSeconds(60), past))).isEmpty();
  }

  @Test
  void garbageTokenRejected() {
    assertThat(service.validate("not-a-jwt")).isEmpty();
    assertThat(service.validate("")).isEmpty();
  }

  @Test
  void wrongSignatureRejected() {
    Instant now = Instant.now();
    String token =
        Jwts.builder()
            .issuer("iss")
            .subject("bob")
            .expiration(Date.from(now.plusSeconds(60)))
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(OTHER_SECRET)))
            .compact();
    assertThat(service.validate(token)).isEmpty();
  }

  @Test
  void wrongIssuerRejected() {
    Instant now = Instant.now();
    JwtService other = new JwtService(new JwtProperties(SECRET, "other", Duration.ofDays(1)));
    assertThat(service.validate(other.generate(user, now, now.plusSeconds(60)))).isEmpty();
  }
}
