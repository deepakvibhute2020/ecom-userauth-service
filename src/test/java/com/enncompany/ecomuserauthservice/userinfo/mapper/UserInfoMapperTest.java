package com.enncompany.ecomuserauthservice.userinfo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import com.enncompany.ecomuserauthservice.entity.UserInfo;
import com.enncompany.ecomuserauthservice.enums.UserType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserInfoMapperTest {

  private final UserInfoMapper mapper = new UserInfoMapper();

  @Test
  void test_userInfo_toDto_mapsAllResponseFields() {
    UserInfo entity = new UserInfo();
    entity.setId(1L);
    entity.setLoginId("alice@example.com");
    entity.setUserType(UserType.ADMIN);
    entity.setUserPass("secret");
    entity.setCountry("USA");
    entity.setCity("New York");
    entity.setContactNo("12345");
    entity.setEmailId("alice@example.com");
    entity.setActive(true);
    entity.setCreatedOn(LocalDateTime.of(2026, 10, 18, 10, 15));
    entity.setModifiedOn(LocalDateTime.of(2026, 10, 18, 11, 30));

    UserInfoDto dto = mapper.toDto(entity);

    assertEquals(1L, dto.getId());
    assertEquals("alice@example.com", dto.getLoginId());
    assertEquals(UserType.ADMIN, dto.getUserType());
    assertEquals("USA", dto.getCountry());
    assertEquals("New York", dto.getCity());
    assertEquals("12345", dto.getContactNo());
    assertEquals("alice@example.com", dto.getEmailId());
    assertEquals(true, dto.getActive());
    assertEquals(LocalDateTime.of(2026, 10, 18, 10, 15), dto.getCreatedOn());
    assertEquals(LocalDateTime.of(2026, 10, 18, 11, 30), dto.getModifiedOn());
    assertNull(dto.getUserPass());
  }

  @Test
  void test_userInfo_toEntity_mapsFieldsAndIgnoresId() {
    UserInfoDto dto = new UserInfoDto();
    dto.setId(99L);
    dto.setLoginId("bob@example.com");
    dto.setUserType(UserType.CUSTOMER);
    dto.setUserPass("secret");
    dto.setCountry("India");
    dto.setCity("Mumbai");
    dto.setContactNo("99999");
    dto.setEmailId("bob@example.com");
    dto.setActive(true);

    UserInfo entity = mapper.toEntity(dto);

    assertNull(entity.getId());
    assertEquals("bob@example.com", entity.getLoginId());
    assertEquals(UserType.CUSTOMER, entity.getUserType());
    assertEquals("secret", entity.getUserPass());
    assertEquals("India", entity.getCountry());
    assertEquals("Mumbai", entity.getCity());
    assertEquals("99999", entity.getContactNo());
    assertEquals("bob@example.com", entity.getEmailId());
    assertEquals(true, entity.getActive());
  }

  @Test
  void test_userInfo_toEntity_trimsStringFields() {
    UserInfoDto dto = new UserInfoDto();
    dto.setLoginId("  bob@example.com  ");
    dto.setUserType(UserType.SUPPLIER);
    dto.setUserPass("  secret  ");
    dto.setCountry("  India  ");
    dto.setCity("  Mumbai  ");
    dto.setContactNo("  99999  ");
    dto.setEmailId("  bob@example.com  ");
    dto.setActive(false);

    UserInfo entity = mapper.toEntity(dto);

    assertEquals("bob@example.com", entity.getLoginId());
    assertEquals("  secret  ", entity.getUserPass());
    assertEquals("India", entity.getCountry());
    assertEquals("Mumbai", entity.getCity());
    assertEquals("99999", entity.getContactNo());
    assertEquals("bob@example.com", entity.getEmailId());
  }
}

