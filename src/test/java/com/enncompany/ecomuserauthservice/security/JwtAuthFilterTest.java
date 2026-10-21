package com.enncompany.ecomuserauthservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.enncompany.ecomuserauthservice.dto.AuthDtos.TokenInfo;
import jakarta.servlet.FilterChain;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtAuthFilterTest {

  private final JwtService jwtService = mock(JwtService.class);
  private final JwtAuthFilter filter = new JwtAuthFilter(jwtService);
  private final FilterChain chain = mock(FilterChain.class);

  @Test
  void loginPathSkipsFilter() throws Exception {
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/auth/login");
    MockHttpServletResponse res = new MockHttpServletResponse();
    filter.doFilter(req, res, chain);
    verify(chain).doFilter(req, res);
    verifyNoInteractions(jwtService);
  }

  @Test
  void validTokenPassesAndSetsAttribute() throws Exception {
    TokenInfo info = new TokenInfo(1L, "bob", 2, Instant.now());
    when(jwtService.validate("tok")).thenReturn(Optional.of(info));
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/x");
    req.addHeader("Authorization", "Bearer tok");
    MockHttpServletResponse res = new MockHttpServletResponse();
    filter.doFilter(req, res, chain);
    verify(chain).doFilter(req, res);
    assertThat(req.getAttribute(JwtAuthFilter.TOKEN_INFO_ATTRIBUTE)).isEqualTo(info);
  }

  @Test
  void missingHeaderRejected() throws Exception {
    assertUnauthorized(new MockHttpServletRequest("GET", "/api/x"));
  }

  @Test
  void nonBearerHeaderRejected() throws Exception {
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/x");
    req.addHeader("Authorization", "Basic abc");
    assertUnauthorized(req);
  }

  @Test
  void invalidTokenRejected() throws Exception {
    when(jwtService.validate("bad")).thenReturn(Optional.empty());
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/x");
    req.addHeader("Authorization", "Bearer bad");
    assertUnauthorized(req);
  }

  private void assertUnauthorized(MockHttpServletRequest req) throws Exception {
    MockHttpServletResponse res = new MockHttpServletResponse();
    filter.doFilter(req, res, chain);
    assertThat(res.getStatus()).isEqualTo(401);
    assertThat(res.getContentAsString()).contains("Invalid or missing token");
    verifyNoInteractions(chain);
  }
}
