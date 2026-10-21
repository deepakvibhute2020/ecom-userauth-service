package com.enncompany.ecomuserauthservice.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserInfoTest {

  @Test
  void onCreate_setsTimestamps() {
    UserInfo user = new UserInfo();
    user.onCreate();
    assertNotNull(user.getCreatedOn());
    assertNotNull(user.getModifiedOn());
  }

  @Test
  void onCreate_keepsExistingCreatedOn() {
    UserInfo user = new UserInfo();
    LocalDateTime created = LocalDateTime.of(2020, 1, 1, 0, 0);
    user.setCreatedOn(created);
    user.onCreate();
    assertEquals(created, user.getCreatedOn());
  }

  @Test
  void onUpdate_refreshesModifiedOn() {
    UserInfo user = new UserInfo();
    user.setModifiedOn(LocalDateTime.of(2020, 1, 1, 0, 0));
    user.onUpdate();
    assertTrue(user.getModifiedOn().isAfter(LocalDateTime.of(2020, 1, 1, 0, 0)));
  }
}