package com.enncompany.ecomuserauthservice.exception;

public class UserInfoNotFoundException extends RuntimeException {

  public UserInfoNotFoundException(Long id) {
    super("UserInfo not found: " + id);
  }

  public UserInfoNotFoundException(String loginId) {
    super("UserInfo not found for loginId: " + loginId);
  }
}

