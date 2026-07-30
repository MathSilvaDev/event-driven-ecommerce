package com.matheus.ecommerce.infrastructure.kafka.order.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    private void sendTemplate(String topic, Long data){
        kafkaTemplate.send(topic, data.toString(), data);
    }

    public void sendOrderCreated(Long orderId){
        sendTemplate("order-created", orderId);
    }

    public void sendOrderPaid(Long orderId) {
        sendTemplate("order-paid", orderId);
    }

    public void sendOrderToPreparing(Long orderId){
        sendTemplate("order-to-preparing", orderId);
    }
}
