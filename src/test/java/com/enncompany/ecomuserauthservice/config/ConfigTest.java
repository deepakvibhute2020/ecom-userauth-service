package com.enncompany.ecomuserauthservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class ConfigTest {

  @Test
  void jwtPropertiesKeepsExplicitExpiration() {
    JwtProperties p = new JwtProperties("s", "i", Duration.ofHours(2));
    assertThat(p.expiration()).isEqualTo(Duration.ofHours(2));
    assertThat(p.secret()).isEqualTo("s");
    assertThat(p.issuer()).isEqualTo("i");
  }

  @Test
  void jwtPropertiesDefaultsToOneDay() {
    assertThat(new JwtProperties("s", "i", null).expiration()).isEqualTo(Duration.ofDays(1));
  }

  @Test
  void appConfigCreatesArgon2PasswordEncoder() {
    var encoder = new AppConfig().passwordEncoder();
    assertThat(encoder.matches("pw", encoder.encode("pw"))).isTrue();
  }

  @Test
  void appConfigCreatesRestClient() {
    RestClient client = new AppConfig().masterdataRestClient("http://localhost:1");
    assertThat(client).isNotNull();
  }
}
