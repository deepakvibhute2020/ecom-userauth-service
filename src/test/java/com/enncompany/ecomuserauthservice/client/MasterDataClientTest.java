package com.enncompany.ecomuserauthservice.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class MasterDataClientTest {

  private MockRestServiceServer server;
  private MasterDataClient client;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://md");
    server = MockRestServiceServer.bindTo(builder).build();
    client = new MasterDataClient(builder.build());
  }

  @Test
  void findByLoginIdReturnsUser() {
    server
        .expect(requestTo("http://md/api/user-info/loginId/bob"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(
            withSuccess(
                "{\"id\":1,\"loginId\":\"bob\",\"userType\":2,\"userPass\":\"h\",\"active\":true,\"x\":1}",
                MediaType.APPLICATION_JSON));
    var user = client.findByLoginId("bob");
    assertThat(user).isPresent();
    assertThat(user.get().loginId()).isEqualTo("bob");
    assertThat(user.get().userPass()).isEqualTo("h");
    server.verify();
  }

  @Test
  void findByLoginIdReturnsEmptyWhenNotFound() {
    server
        .expect(requestTo("http://md/api/user-info/loginId/x"))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));
    assertThat(client.findByLoginId("x")).isEmpty();
  }
}
