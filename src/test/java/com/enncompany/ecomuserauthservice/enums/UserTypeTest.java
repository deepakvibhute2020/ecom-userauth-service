package com.enncompany.ecomuserauthservice.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserTypeTest {

  @Test
  void fromCode_mapsAllCodes() {
    for (UserType type : UserType.values()) {
      assertEquals(type, UserType.fromCode(type.getCode()));
    }
  }

  @Test
  void fromCode_nullReturnsNull() {
    assertNull(UserType.fromCode(null));
  }

  @Test
  void fromCode_unknownThrows() {
    assertThrows(IllegalArgumentException.class, () -> UserType.fromCode(9));
  }
}