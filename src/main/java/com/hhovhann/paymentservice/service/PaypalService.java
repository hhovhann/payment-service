package com.hhovhann.paymentservice.service;

import org.springframework.http.ResponseEntity;

public interface PaypalService {
    String BASE_URL = "https://api-m.sandbox.paypal.com";
    ResponseEntity<Object> createOrder();
    ResponseEntity<Object> capturePayment(String orderId);
}
