package com.enncompany.ecomuserauthservice.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, String issuer, Duration expiration) {

  public JwtProperties {
    if (expiration == null) {
      expiration = Duration.ofDays(1);
    }
  }
}
