package com.enncompany.ecomuserauthservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserInfoDto(
    Long id,
    String loginId,
    Integer userType,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String userPass,
    String emailId,
    Boolean active) {}
