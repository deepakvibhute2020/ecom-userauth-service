package com.enncompany.ecomuserauthservice.userinfo.mapper;

import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import com.enncompany.ecomuserauthservice.entity.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserInfoMapper {

  public UserInfoDto toDto(UserInfo entity) {
    UserInfoDto dto = new UserInfoDto();
    dto.setId(entity.getId());
    dto.setLoginId(entity.getLoginId());
    dto.setUserType(entity.getUserType());
    dto.setCountry(entity.getCountry());
    dto.setCity(entity.getCity());
    dto.setContactNo(entity.getContactNo());
    dto.setEmailId(entity.getEmailId());
    dto.setActive(entity.getActive());
    dto.setCreatedOn(entity.getCreatedOn());
    dto.setModifiedOn(entity.getModifiedOn());
    return dto;
  }

  public UserInfo toEntity(UserInfoDto dto) {
    UserInfo entity = new UserInfo();
    apply(entity, dto);
    return entity;
  }

  public void apply(UserInfo entity, UserInfoDto dto) {
    entity.setLoginId(trim(dto.getLoginId()));
    entity.setUserType(dto.getUserType());
    entity.setUserPass(dto.getUserPass());
    entity.setCountry(trim(dto.getCountry()));
    entity.setCity(trim(dto.getCity()));
    entity.setContactNo(trim(dto.getContactNo()));
    entity.setEmailId(trim(dto.getEmailId()));
    entity.setActive(dto.getActive());
  }

  private String trim(String value) {
    return value == null ? null : value.trim();
  }
}

