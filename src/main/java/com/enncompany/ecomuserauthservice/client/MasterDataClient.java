package com.enncompany.ecomuserauthservice.client;

import com.enncompany.ecomuserauthservice.dto.UserInfoDto;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MasterDataClient {

  private final RestClient restClient;

  public MasterDataClient(RestClient masterdataRestClient) {
    this.restClient = masterdataRestClient;
  }

  public Optional<UserInfoDto> findByLoginId(String loginId) {
    return Optional.ofNullable(
        restClient
            .get()
            .uri("/api/user-info/loginId/{loginId}", loginId)
            .exchange(
                (req, res) ->
                    res.getStatusCode().is2xxSuccessful() ? res.bodyTo(UserInfoDto.class) : null));
  }
}
