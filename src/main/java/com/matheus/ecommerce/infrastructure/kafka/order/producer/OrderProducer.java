package com.matheus.ecommerce.infrastructure.kafka.order.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private void sendTemplate(String topic, String key, String data){
        kafkaTemplate.send(topic, key, data);
    }

    public void sendOrderCreated(String orderId){
        sendTemplate("order-created", orderId, orderId);
    }

    public void sendOrderPaid(String orderId) {
        sendTemplate("order-paid", orderId, orderId);
    }
}
