package com.enncompany.ecomuserauthservice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

  @Test
  void test_openApi_declaresBearerJwtScheme() {
    OpenAPI api = new OpenApiConfig().openApi();

    assertEquals("bearer", api.getComponents().getSecuritySchemes().get("bearerAuth").getScheme());
    assertTrue(api.getSecurity().get(0).containsKey("bearerAuth"));
  }
}
