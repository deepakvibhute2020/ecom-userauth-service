package com.enncompany.ecomuserauthservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class AppConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  @Bean
  public RestClient masterdataRestClient(@Value("${app.masterdata.base-url}") String baseUrl) {
    return RestClient.create(baseUrl);
  }
}
