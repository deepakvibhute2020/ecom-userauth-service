package com.enncompany.ecomuserauthservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EcomUserauthServiceApplicationTests {

  @Test
  void contextLoads() {}

  @Test
  void mainStartsApplication() {
    EcomUserauthServiceApplication.main(
        new String[] {"--server.port=0", "--spring.profiles.active=test"});
  }
}
