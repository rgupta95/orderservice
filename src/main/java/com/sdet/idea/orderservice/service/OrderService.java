package com.sdet.idea.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdet.idea.orderservice.common.Payment;
import com.sdet.idea.orderservice.common.TransactionRequest;
import com.sdet.idea.orderservice.common.TransactionResponse;
import com.sdet.idea.orderservice.entity.Order;
import com.sdet.idea.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
public class OrderService {
@Autowired
    private OrderRepository repository;
@Autowired
@Lazy
private RestTemplate template;
    @Value("${microservice.payment-service.endpoints.endpoint.uri}")
    private String ENDPOINT_URL;
    private Logger logger = LoggerFactory.getLogger(OrderService.class);
public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
    String response = "";
    Order order= request.getOrder();
    Payment payment = request.getPayment();
    payment.setOrderId(order.getId());
    payment.setAmount(order.getPrice());
    logger.info("orderService request: {}", new ObjectMapper().writeValueAsString(request));
    //rest call
Payment paymentResponse =template.postForObject(ENDPOINT_URL, payment, Payment.class);
    logger.info("Payment-service response from order service rest call: {}", new ObjectMapper().writeValueAsString(paymentResponse));
    response=  paymentResponse.getPaymentStatus().equals("success")?"payment processing successful and order placed":"there is a failure in payment api, order added to cart";
     repository.save(order);
     return new TransactionResponse(order,paymentResponse.getAmount(), paymentResponse.getTransactionId(),response );
}
}
