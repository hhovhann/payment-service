package com.hhovhann.paymentservice.controller;

import com.hhovhann.paymentservice.service.PaypalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController("")
public class PaypalController {
  private final PaypalService paymentService;

  @PostMapping(value = "/api/orders")
  @CrossOrigin
  public Object createOrder() {
    ResponseEntity<Object> response = paymentService.createOrder();
    if (response.getStatusCode() == CREATED) {
      log.info("ORDER CREATED");
      return response.getBody();
    } else {
      log.info("FAILED CREATING ORDER");
      return "Unavailable to get CREATE AN ORDER, STATUS CODE " + response.getStatusCode();
    }
  }

  public PaypalController(PaypalService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping(value = "/api/orders/{orderId}/capture")
  @CrossOrigin
  public Object capturePayment(@PathVariable("orderId") String orderId) {
    ResponseEntity<Object> response = paymentService.capturePayment(orderId);
    if (response.getStatusCode() == CREATED) {
      log.info("ORDER CAPTURE");
      return response.getBody();
    } else {
      log.info("FAILED CAPTURING ORDER");
      return "Unavailable to get CAPTURE ORDER, STATUS CODE " + response.getStatusCode();
    }
  }
}
