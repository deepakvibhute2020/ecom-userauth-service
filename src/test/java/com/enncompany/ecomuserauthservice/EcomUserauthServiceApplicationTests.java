package com.enncompany.ecomuserauthservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcomUserauthServiceApplicationTests {

  @Test
  void contextLoads() {}

  @Test
  void mainStartsApplication() {
    EcomUserauthServiceApplication.main(new String[] {"--server.port=0"});
  }
}
