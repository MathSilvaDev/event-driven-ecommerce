package com.matheus.ecommerce.infrastructure.kafka.order.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.matheus.ecommerce.infrastructure.kafka.order.dto.OrderKafkaResponse;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderKafkaResponse> kafkaTemplate;

    public void sendOrderStatus(OrderKafkaResponse response){
        kafkaTemplate.send(
            "order-status", 
            response.orderId().toString(), 
            response);
    }

}
