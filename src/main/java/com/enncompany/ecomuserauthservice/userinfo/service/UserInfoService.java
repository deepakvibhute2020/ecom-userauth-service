package com.enncompany.ecomuserauthservice.userinfo.service;

import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserInfoService {

  UserInfoDto create(UserInfoDto dto);

  UserInfoDto getById(Long id);

  UserInfoDto getByLoginId(String loginId);

  Page<UserInfoDto> getAll(Pageable pageable);

  UserInfoDto update(Long id, UserInfoDto dto);
}

