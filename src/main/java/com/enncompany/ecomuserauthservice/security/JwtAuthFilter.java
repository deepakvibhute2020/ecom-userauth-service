package com.enncompany.ecomuserauthservice.security;

import com.enncompany.ecomuserauthservice.dto.AuthDtos.TokenInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Requires a valid Bearer JWT on every request except login. */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  public static final String TOKEN_INFO_ATTRIBUTE = "tokenInfo";
  private static final String LOGIN_PATH = "/api/auth/login";
  private static final String USER_INFO_PATH = "/api/user-info";
  private static final String API_DOCS_PATH = "/api-docs";
  private static final String BEARER = "Bearer ";

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return LOGIN_PATH.equals(uri)
        || API_DOCS_PATH.equals(uri)
        || ("POST".equals(request.getMethod()) && USER_INFO_PATH.equals(uri));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    Optional<TokenInfo> info =
        header != null && header.startsWith(BEARER)
            ? jwtService.validate(header.substring(BEARER.length()).trim())
            : Optional.empty();
    if (info.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"error\":\"Invalid or missing token\"}");
      return;
    }
    request.setAttribute(TOKEN_INFO_ATTRIBUTE, info.get());
    chain.doFilter(request, response);
  }
}
