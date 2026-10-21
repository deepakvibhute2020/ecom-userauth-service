package com.enncompany.ecomuserauthservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {

  ADMIN(0),
  SUPPLIER(1),
  CUSTOMER(2);

  private final int code;

  UserType(int code) {
    this.code = code;
  }

  @JsonValue
  public int getCode() {
    return code;
  }

  @JsonCreator
  public static UserType fromCode(Integer code) {
    if (code == null) {
      return null;
    }

    for (UserType userType : values()) {
      if (userType.code == code) {
        return userType;
      }
    }

    throw new IllegalArgumentException("Invalid user type: " + code);
  }
}

