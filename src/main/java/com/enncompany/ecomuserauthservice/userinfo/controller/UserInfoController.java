package com.enncompany.ecomuserauthservice.userinfo.controller;

import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import com.enncompany.ecomuserauthservice.userinfo.service.UserInfoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-info")
public class UserInfoController {

  private final UserInfoService service;

  public UserInfoController(UserInfoService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<UserInfoDto> create(@Valid @RequestBody UserInfoDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserInfoDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getById(id));
  }

  @GetMapping("/loginId/{loginId}")
  public ResponseEntity<UserInfoDto> getByLoginId(@PathVariable String loginId) {
    return ResponseEntity.ok(service.getByLoginId(loginId));
  }

  @GetMapping
  public ResponseEntity<PagedModel<UserInfoDto>> getAll(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(new PagedModel<>(service.getAll(pageable)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserInfoDto> update(
      @PathVariable Long id, @Valid @RequestBody UserInfoDto dto) {
    return ResponseEntity.ok(service.update(id, dto));
  }
}

