package com.enncompany.ecomuserauthservice.userinfo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import com.enncompany.ecomuserauthservice.enums.UserType;
import com.enncompany.ecomuserauthservice.exception.GlobalExceptionHandler;
import com.enncompany.ecomuserauthservice.exception.UserInfoNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserInfoControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private com.enncompany.ecomuserauthservice.security.JwtService jwtService;
  @MockitoBean private com.enncompany.ecomuserauthservice.userinfo.service.UserInfoService service;

  private static UserInfoDto dto(
      Long id,
      String loginId,
      UserType userType,
      String userPass,
      String country,
      String city,
      String contactNo,
      String emailId,
      Boolean active) {
    UserInfoDto dto = new UserInfoDto();
    dto.setId(id);
    dto.setLoginId(loginId);
    dto.setUserType(userType);
    dto.setUserPass(userPass);
    dto.setCountry(country);
    dto.setCity(city);
    dto.setContactNo(contactNo);
    dto.setEmailId(emailId);
    dto.setActive(active);
    dto.setCreatedOn(LocalDateTime.of(2026, 10, 18, 10, 15));
    dto.setModifiedOn(LocalDateTime.of(2026, 10, 18, 11, 30));
    return dto;
  }

  private static String json(
      String loginId,
      Integer userType,
      String userPass,
      String country,
      String city,
      String contactNo,
      String emailId,
      Boolean active) {
    return "{"
        + "\"loginId\":" + (loginId == null ? "null" : "\"" + loginId + "\"") + ","
        + "\"userType\":" + (userType == null ? "null" : userType) + ","
        + "\"userPass\":" + (userPass == null ? "null" : "\"" + userPass + "\"") + ","
        + "\"country\":" + (country == null ? "null" : "\"" + country + "\"") + ","
        + "\"city\":" + (city == null ? "null" : "\"" + city + "\"") + ","
        + "\"contactNo\":" + (contactNo == null ? "null" : "\"" + contactNo + "\"") + ","
        + "\"emailId\":" + (emailId == null ? "null" : "\"" + emailId + "\"") + ","
        + "\"active\":" + (active == null ? "null" : active)
        + "}";
  }

  @Test
  void test_userInfo_create_returnsCreated() throws Exception {
    when(service.create(any(UserInfoDto.class)))
        .thenReturn(
            dto(
                1L,
                "alice@example.com",
                UserType.ADMIN,
                "secret",
                "USA",
                "New York",
                "12345",
                "alice@example.com",
                true));

    mockMvc
        .perform(
            post("/api/user-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json(
                        "alice@example.com",
                        0,
                        "secret",
                        "USA",
                        "New York",
                        "12345",
                        "alice@example.com",
                        true)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.loginId").value("alice@example.com"))
        .andExpect(jsonPath("$.userType").value(0))
        .andExpect(jsonPath("$.country").value("USA"))
        .andExpect(jsonPath("$.city").value("New York"))
        .andExpect(jsonPath("$.contactNo").value("12345"))
        .andExpect(jsonPath("$.emailId").value("alice@example.com"))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.userPass").doesNotExist());

    verify(service).create(any(UserInfoDto.class));
  }

  @Test
  void test_userInfo_create_missingLoginId_badRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/user-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(null, 0, "secret", "USA", "New York", "12345", "alice@example.com", true)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("loginId is required"));

    verify(service, never()).create(any());
  }

  @Test
  void test_userInfo_create_invalidUserType_badRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/user-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("alice@example.com", 9, "secret", "USA", "New York", "12345", "alice@example.com", true)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed request body"));

    verify(service, never()).create(any());
  }

  @Test
  void test_userInfo_create_malformedJson_badRequest() throws Exception {
    mockMvc
        .perform(post("/api/user-info").contentType(MediaType.APPLICATION_JSON).content("{bad"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed request body"));

    verify(service, never()).create(any());
  }

  @Test
  void test_userInfo_getById_exists_ok() throws Exception {
    when(service.getById(101L))
        .thenReturn(
            dto(
                101L,
                "alice@example.com",
                UserType.ADMIN,
                "secret",
                "USA",
                "New York",
                "12345",
                "alice@example.com",
                true));

    mockMvc
        .perform(get("/api/user-info/101"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(101))
        .andExpect(jsonPath("$.loginId").value("alice@example.com"))
        .andExpect(jsonPath("$.userType").value(0))
        .andExpect(jsonPath("$.userPass").doesNotExist());

    verify(service).getById(101L);
  }

  @Test
  void test_userInfo_getById_notFound() throws Exception {
    when(service.getById(9999L)).thenThrow(new UserInfoNotFoundException(9999L));

    mockMvc
        .perform(get("/api/user-info/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("UserInfo not found: 9999"));
  }

  @Test
  void test_userInfo_getById_invalidId_badRequest() throws Exception {
    mockMvc
        .perform(get("/api/user-info/abc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid value for parameter: id"));
  }
  @Test
  void test_userInfo_getByLoginId_ok() throws Exception {
    when(service.getByLoginId("alice@example.com"))
        .thenReturn(
            dto(1L, "alice@example.com", UserType.ADMIN, "hash", "USA", "NY", "1", "a@x.com", true));

    mockMvc
        .perform(get("/api/user-info/loginId/alice@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.loginId").value("alice@example.com"))
        .andExpect(jsonPath("$.userPass").doesNotExist())
        .andExpect(jsonPath("$.userType").value(0));
  }

  @Test
  void test_userInfo_getByLoginId_notFound() throws Exception {
    when(service.getByLoginId("x")).thenThrow(new UserInfoNotFoundException("x"));

    mockMvc.perform(get("/api/user-info/loginId/x")).andExpect(status().isNotFound());
  }

  @Test
  void test_userInfo_getAll_ok() throws Exception {
    when(service.getAll(any()))
        .thenReturn(
            new PageImpl<>(
                List.of(
                    dto(
                        1L,
                        "alice@example.com",
                        UserType.ADMIN,
                        "secret",
                        "USA",
                        "New York",
                        "12345",
                        "alice@example.com",
                        true),
                    dto(
                        2L,
                        "bob@example.com",
                        UserType.CUSTOMER,
                        "secret2",
                        "India",
                        "Mumbai",
                        "99999",
                        "bob@example.com",
                        false)),
                PageRequest.of(0, 20),
                2));

    mockMvc
        .perform(get("/api/user-info"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[1].loginId").value("bob@example.com"))
        .andExpect(jsonPath("$.page.size").value(20))
        .andExpect(jsonPath("$.page.totalElements").value(2));

    verify(service).getAll(PageRequest.of(0, 20));
  }

  @Test
  void test_userInfo_update_ok() throws Exception {
    when(service.update(any(), any(UserInfoDto.class)))
        .thenReturn(
            dto(
                1L,
                "alice.updated@example.com",
                UserType.CUSTOMER,
                "secret",
                "UK",
                "London",
                "77777",
                "alice.updated@example.com",
                true));

    mockMvc
        .perform(
            put("/api/user-info/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json(
                        "alice.updated@example.com",
                        2,
                        "secret",
                        "UK",
                        "London",
                        "77777",
                        "alice.updated@example.com",
                        true)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.loginId").value("alice.updated@example.com"))
        .andExpect(jsonPath("$.userType").value(2))
        .andExpect(jsonPath("$.country").value("UK"))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.userPass").doesNotExist());

    verify(service).update(any(), any(UserInfoDto.class));
  }

  @Test
  void test_userInfo_update_notFound() throws Exception {
    when(service.update(any(), any(UserInfoDto.class))).thenThrow(new UserInfoNotFoundException(5L));

    mockMvc
        .perform(
            put("/api/user-info/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("x", 1, "secret", "Y", "Z", "123", "x@y.com", true)))
        .andExpect(status().isNotFound());
  }

  @Test
  void test_userInfo_update_blankLoginId_badRequest() throws Exception {
    mockMvc
        .perform(
            put("/api/user-info/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("", 1, "secret", "USA", "New York", "12345", "alice@example.com", true)))
        .andExpect(status().isBadRequest());

    verify(service, never()).update(any(), any());
  }
}

