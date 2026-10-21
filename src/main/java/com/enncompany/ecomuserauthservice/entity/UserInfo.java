package com.enncompany.ecomuserauthservice.entity;

import com.enncompany.ecomuserauthservice.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_info")
@Getter
@Setter
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "login_id", nullable = false, length = 200)
  private String loginId;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "user_type", nullable = false)
  private UserType userType;

  @Column(name = "user_pass", nullable = false, length = 255)
  private String userPass;

  @Column(length = 200)
  private String country;

  @Column(length = 200)
  private String city;

  @Column(name = "contact_no", length = 20)
  private String contactNo;

  @Column(name = "email_id", length = 100)
  private String emailId;

  @Column(nullable = false)
  private Boolean active;

  @Column(name = "created_on", nullable = false)
  private LocalDateTime createdOn;

  @Column(name = "modified_on", nullable = false)
  private LocalDateTime modifiedOn;

  @PrePersist
  void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    if (createdOn == null) {
      createdOn = now;
    }
    modifiedOn = now;
  }

  @PreUpdate
  void onUpdate() {
    modifiedOn = LocalDateTime.now();
  }
}

