package com.enncompany.ecomuserauthservice.dto;

import com.enncompany.ecomuserauthservice.enums.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDto {

  private Long id;

  @NotBlank(message = "loginId is required")
  @Size(max = 200, message = "loginId must be <= 200 characters")
  private String loginId;

  @NotNull(message = "userType is required")
  private UserType userType;

  @NotBlank(message = "userPass is required")
  @Size(max = 255, message = "userPass must be <= 255 characters")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String userPass;

  @Size(max = 200, message = "country must be <= 200 characters")
  private String country;

  @Size(max = 200, message = "city must be <= 200 characters")
  private String city;

  @Size(max = 20, message = "contactNo must be <= 20 characters")
  private String contactNo;

  @Email(message = "emailId must be valid")
  @Size(max = 100, message = "emailId must be <= 100 characters")
  private String emailId;

  @NotNull(message = "active is required")
  private Boolean active;

  private LocalDateTime createdOn;

  private LocalDateTime modifiedOn;
}

