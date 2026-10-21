package com.enncompany.ecomuserauthservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enncompany.ecomuserauthservice.dto.AuthDtos.ErrorResponse;
import com.enncompany.ecomuserauthservice.dto.AuthDtos.LoginRequest;
import com.enncompany.ecomuserauthservice.dto.AuthDtos.LoginResponse;
import com.enncompany.ecomuserauthservice.dto.AuthDtos.TokenInfo;
import com.enncompany.ecomuserauthservice.entity.UserInfo;
import com.enncompany.ecomuserauthservice.enums.UserType;
import com.enncompany.ecomuserauthservice.security.JwtService;
import com.enncompany.ecomuserauthservice.security.PasswordDecryptor;
import com.enncompany.ecomuserauthservice.userinfo.mapper.UserInfoMapper;
import com.enncompany.ecomuserauthservice.userinfo.repository.UserInfoRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthControllerTest {

  private final UserInfoRepository repository = mock(UserInfoRepository.class);
  private final JwtService jwt = mock(JwtService.class);
  private final PasswordDecryptor decryptor = mock(PasswordDecryptor.class);
  private final PasswordEncoder encoder = mock(PasswordEncoder.class);
  private final AuthController controller =
      new AuthController(repository, new UserInfoMapper(), jwt, decryptor, encoder);
  private final UserInfo user = user();

  private static UserInfo user() {
    UserInfo u = new UserInfo();
    u.setId(1L);
    u.setLoginId("bob");
    u.setUserType(UserType.CUSTOMER);
    u.setUserPass("hash");
    u.setEmailId("b@x.com");
    u.setActive(true);
    return u;
  }

  private String message(LoginRequest r) {
    return ((ErrorResponse) controller.login(r).getBody()).message();
  }

  private int status(LoginRequest r) {
    return controller.login(r).getStatusCode().value();
  }

  @Test
  void loginSuccess() {
    Instant exp = Instant.now().plusSeconds(60);
    when(repository.findByLoginIdAndActiveTrue("bob")).thenReturn(List.of(user));
    when(decryptor.decrypt("enc")).thenReturn(Optional.of("plain"));
    when(encoder.matches("plain", "hash")).thenReturn(true);
    when(jwt.expiryFor(any())).thenReturn(exp);
    when(jwt.generate(any(), any(), any())).thenReturn("tok");

    ResponseEntity<?> res = controller.login(new LoginRequest("bob", "enc"));
    LoginResponse body = (LoginResponse) res.getBody();

    assertThat(res.getStatusCode().value()).isEqualTo(200);
    assertThat(body.token()).isEqualTo("tok");
    assertThat(body.tokenType()).isEqualTo("Bearer");
    assertThat(body.expiresAt()).isEqualTo(exp);
    assertThat(body.user().getLoginId()).isEqualTo("bob");
    assertThat(body.user().getUserType()).isEqualTo(UserType.CUSTOMER);
  }

  @Test
  void blankOrNullFieldsBadRequest() {
    assertThat(status(new LoginRequest(null, "pw"))).isEqualTo(400);
    assertThat(status(new LoginRequest(" ", "pw"))).isEqualTo(400);
    assertThat(message(new LoginRequest("bob", null))).contains("required");
    assertThat(status(new LoginRequest("bob", null))).isEqualTo(400);
    assertThat(status(new LoginRequest("bob", " "))).isEqualTo(400);
    verify(repository, never()).findByLoginIdAndActiveTrue(any());
  }

  @Test
  void unknownOrInactiveUserUnauthorized() {
    when(repository.findByLoginIdAndActiveTrue("bob")).thenReturn(List.of());
    assertThat(status(new LoginRequest("bob", "enc"))).isEqualTo(401);
    assertThat(message(new LoginRequest("bob", "enc"))).isEqualTo("User not found");
    verify(decryptor, never()).decrypt(any());
  }

  @Test
  void undecryptablePasswordUnauthorized() {
    when(repository.findByLoginIdAndActiveTrue("bob")).thenReturn(List.of(user));
    when(decryptor.decrypt("enc")).thenReturn(Optional.empty());
    assertThat(status(new LoginRequest("bob", "enc"))).isEqualTo(401);
  }

  @Test
  void wrongPasswordUnauthorized() {
    when(repository.findByLoginIdAndActiveTrue("bob")).thenReturn(List.of(user));
    when(decryptor.decrypt("enc")).thenReturn(Optional.of("plain"));
    when(encoder.matches("plain", "hash")).thenReturn(false);
    assertThat(status(new LoginRequest("bob", "enc"))).isEqualTo(401);
    assertThat(message(new LoginRequest("bob", "enc"))).isEqualTo("User credential does not match");
    verify(jwt, never()).generate(any(), any(), any());
  }

  @Test
  void validateReturnsTokenInfo() {
    TokenInfo info = new TokenInfo(1L, "bob", 2, Instant.now());
    assertThat(controller.validate(info)).isSameAs(info);
  }
}
