package com.enncompany.ecomuserauthservice.userinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enncompany.ecomuserauthservice.config.AppConfig;
import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import com.enncompany.ecomuserauthservice.entity.UserInfo;
import com.enncompany.ecomuserauthservice.enums.UserType;
import com.enncompany.ecomuserauthservice.exception.UserInfoNotFoundException;
import com.enncompany.ecomuserauthservice.userinfo.mapper.UserInfoMapper;
import com.enncompany.ecomuserauthservice.userinfo.repository.UserInfoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceImplTest {

  @Mock private UserInfoRepository repository;
  private PasswordEncoder passwordEncoder;
  private UserInfoServiceImpl service;

  @BeforeEach
  void setUp() {
    passwordEncoder = new AppConfig().passwordEncoder();
    service = new UserInfoServiceImpl(repository, new UserInfoMapper(), passwordEncoder);
  }

  private static UserInfo userInfo(
      Long id,
      String loginId,
      UserType userType,
      String userPass,
      String country,
      String city,
      String contactNo,
      String emailId,
      Boolean active) {
    UserInfo userInfo = new UserInfo();
    userInfo.setId(id);
    userInfo.setLoginId(loginId);
    userInfo.setUserType(userType);
    userInfo.setUserPass(userPass);
    userInfo.setCountry(country);
    userInfo.setCity(city);
    userInfo.setContactNo(contactNo);
    userInfo.setEmailId(emailId);
    userInfo.setActive(active);
    userInfo.setCreatedOn(LocalDateTime.of(2026, 10, 18, 10, 15));
    userInfo.setModifiedOn(LocalDateTime.of(2026, 10, 18, 11, 30));
    return userInfo;
  }

  private static UserInfoDto dto(
      String loginId,
      UserType userType,
      String userPass,
      String country,
      String city,
      String contactNo,
      String emailId,
      Boolean active) {
    UserInfoDto dto = new UserInfoDto();
    dto.setLoginId(loginId);
    dto.setUserType(userType);
    dto.setUserPass(userPass);
    dto.setCountry(country);
    dto.setCity(city);
    dto.setContactNo(contactNo);
    dto.setEmailId(emailId);
    dto.setActive(active);
    return dto;
  }

  @Test
  void test_userInfo_create_success() {
    when(repository.save(any(UserInfo.class)))
        .thenReturn(
            userInfo(
                1L,
                "alice@example.com",
                UserType.ADMIN,
                "secret",
                "USA",
                "New York",
                "12345",
                "alice@example.com",
                true));

    UserInfoDto result =
        service.create(
            dto(
                "alice@example.com",
                UserType.ADMIN,
                "secret",
                "USA",
                "New York",
                "12345",
                "alice@example.com",
                true));

    assertEquals(1L, result.getId());
    assertEquals("alice@example.com", result.getLoginId());
    assertEquals(UserType.ADMIN, result.getUserType());
    assertEquals("USA", result.getCountry());
    assertEquals("New York", result.getCity());
    assertEquals("12345", result.getContactNo());
    assertEquals("alice@example.com", result.getEmailId());
    assertEquals(true, result.getActive());

    ArgumentCaptor<UserInfo> captor = ArgumentCaptor.forClass(UserInfo.class);
    verify(repository).save(captor.capture());
    assertTrue(passwordEncoder.matches("secret", captor.getValue().getUserPass()));
  }

  @Test
  void test_userInfo_getById_success() {
    when(repository.findById(1L))
        .thenReturn(
            Optional.of(
                userInfo(
                    1L,
                    "alice@example.com",
                    UserType.ADMIN,
                    "secret",
                    "USA",
                    "New York",
                    "12345",
                    "alice@example.com",
                    true)));

    UserInfoDto result = service.getById(1L);

    assertEquals(1L, result.getId());
    assertEquals("alice@example.com", result.getLoginId());
    assertEquals(UserType.ADMIN, result.getUserType());
    verify(repository).findById(1L);
  }

  @Test
  void test_userInfo_getById_notFound() {
    when(repository.findById(5L)).thenReturn(Optional.empty());

    UserInfoNotFoundException ex = assertThrows(UserInfoNotFoundException.class, () -> service.getById(5L));

    assertEquals("UserInfo not found: 5", ex.getMessage());
  }

  @Test
  void test_userInfo_getByLoginId_success() {
    when(repository.findByLoginIdAndActiveTrue("alice@example.com"))
        .thenReturn(
            List.of(
                userInfo(
                    1L,
                    "alice@example.com",
                    UserType.ADMIN,
                    "hash",
                    "USA",
                    "NY",
                    "1",
                    "alice@example.com",
                    true)));

    UserInfoDto result = service.getByLoginId("alice@example.com");

    assertEquals(1L, result.getId());
    assertNull(result.getUserPass());
    assertEquals(UserType.ADMIN, result.getUserType());
    assertEquals("alice@example.com", result.getEmailId());
    assertEquals(true, result.getActive());
  }

  @Test
  void test_userInfo_getByLoginId_notFound() {
    when(repository.findByLoginIdAndActiveTrue("x")).thenReturn(List.of());

    UserInfoNotFoundException ex =
        assertThrows(UserInfoNotFoundException.class, () -> service.getByLoginId("x"));

    assertEquals("UserInfo not found for loginId: x", ex.getMessage());
  }

  @Test
  void test_userInfo_getAll_success() {
    when(repository.findAll(any(Pageable.class)))
        .thenReturn(
            new PageImpl<>(
                List.of(
                    userInfo(
                        1L,
                        "alice@example.com",
                        UserType.ADMIN,
                        "secret",
                        "USA",
                        "New York",
                        "12345",
                        "alice@example.com",
                        true),
                    userInfo(
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

    Page<UserInfoDto> result = service.getAll(PageRequest.of(0, 20));

    assertEquals(2, result.getContent().size());
    assertEquals("alice@example.com", result.getContent().get(0).getLoginId());
    assertEquals("bob@example.com", result.getContent().get(1).getLoginId());
    verify(repository).findAll(PageRequest.of(0, 20));
  }

  @Test
  void test_userInfo_getAll_empty() {
    when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

    assertEquals(0, service.getAll(PageRequest.of(0, 20)).getContent().size());
  }

  @Test
  void test_userInfo_update_trimsFields() {
    UserInfo existing =
        userInfo(
            1L,
            "old@example.com",
            UserType.SUPPLIER,
            "old-secret",
            "OldCountry",
            "OldCity",
            "00000",
            "old@example.com",
            false);
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    UserInfoDto result =
        service.update(
            1L,
            dto(
                "  new@example.com  ",
                UserType.CUSTOMER,
                "  new-secret  ",
                "  UK  ",
                "  London  ",
                "  11111  ",
                "  new@example.com  ",
                true));

    assertEquals("new@example.com", result.getLoginId());
    assertEquals(UserType.CUSTOMER, result.getUserType());
    assertEquals("UK", result.getCountry());
    assertEquals("London", result.getCity());
    assertEquals("11111", result.getContactNo());
    assertEquals("new@example.com", result.getEmailId());
    assertEquals(true, result.getActive());

    ArgumentCaptor<UserInfo> captor = ArgumentCaptor.forClass(UserInfo.class);
    verify(repository).save(captor.capture());
    assertTrue(passwordEncoder.matches("  new-secret  ", captor.getValue().getUserPass()));
  }

  @Test
  void test_userInfo_update_notFound() {
    when(repository.findById(5L)).thenReturn(Optional.empty());

    assertThrows(
        UserInfoNotFoundException.class,
        () ->
            service.update(
                5L,
                dto(
                    "new@example.com",
                    UserType.CUSTOMER,
                    "new-secret",
                    "UK",
                    "London",
                    "11111",
                    "new@example.com",
                    true)));

    verify(repository, never()).save(any());
  }
}

