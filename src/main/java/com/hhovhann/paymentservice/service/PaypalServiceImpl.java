package com.hhovhann.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
public class PaypalServiceImpl implements PaypalService {
  @Value("#{environment.CLIENT_ID}")
  private String clientId;

  @Value("#{environment.APPLICATION_SECRET_ID}")
  private String secretId;

  @Override
  public ResponseEntity<Object> createOrder() {
    String accessToken = generateAccessToken();
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.add("Content-Type", APPLICATION_JSON_VALUE);
    headers.add("Accept", APPLICATION_JSON_VALUE);
    headers.setContentType(APPLICATION_JSON);

    // JSON String
    String requestJson =
        """
           {
              "intent": "CAPTURE",
              "purchase_units": [
                  {
                    "amount": { "currency_code": "USD", "value": "100.00" }
                  }
              ]
           }
           """;
    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

    return restTemplate.exchange(BASE_URL + "/v2/checkout/orders", POST, entity, Object.class);
  }

  @Override
  public ResponseEntity<Object> capturePayment(String orderId) {
    String accessToken = generateAccessToken();
    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();

    headers.set("Authorization", "Bearer " + accessToken);
    headers.add("Content-Type", APPLICATION_JSON_VALUE);
    headers.add("Accept", APPLICATION_JSON_VALUE);
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    return restTemplate.exchange(
        BASE_URL + "/v2/checkout/orders/" + orderId + "/capture", POST, entity, Object.class);
  }

  private String getAuth(String client_id, String app_secret) {
    String auth = client_id + ":" + app_secret;
    return Base64.getEncoder().encodeToString(auth.getBytes());
  }

  private String generateAccessToken() {
    String auth = getAuth(clientId, secretId);
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set("Authorization", "Basic " + auth);

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    HttpEntity<?> request = new HttpEntity<>(requestBody, headers);
    requestBody.add("grant_type", "client_credentials");

    ResponseEntity<String> response =
        restTemplate.postForEntity(BASE_URL + "/v1/oauth2/token", request, String.class);

    if (response.getStatusCode() == HttpStatus.OK) {
      log.info("GET TOKEN: SUCCESSFUL!");
      return new JSONObject(response.getBody()).getString("access_token");
    } else {
      log.info("GET TOKEN: FAILED!");
      return "Unavailable to get ACCESS TOKEN, STATUS CODE " + response.getStatusCode();
    }
  }
}
