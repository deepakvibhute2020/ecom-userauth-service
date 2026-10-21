package com.enncompany.ecomuserauthservice.userinfo.repository;

import com.enncompany.ecomuserauthservice.entity.UserInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

  List<UserInfo> findByLoginIdAndActiveTrue(String loginId);
}

