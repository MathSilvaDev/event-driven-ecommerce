package com.matheus.ecommerce.infrastructure.kafka.order.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderCreated(String orderId){
        kafkaTemplate.send(
                "order-created",
                orderId,
                orderId
        );
    }
}
