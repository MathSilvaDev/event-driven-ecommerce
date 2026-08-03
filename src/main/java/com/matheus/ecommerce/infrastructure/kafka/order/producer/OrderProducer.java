package com.matheus.ecommerce.infrastructure.kafka.order.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    private void sendTemplate(String topic, Long orderId){
        kafkaTemplate.send(topic, orderId.toString(), orderId);
    }

    public void sendOrderCreated(Long orderId){
        sendTemplate("order-created", orderId);
    }

    public void sendOrderPaid(Long orderId) {
        sendTemplate("order-paid", orderId);
    }

    public void sendOrderCanceled(Long orderId){
        sendTemplate("order-canceled", orderId);
    }

    public void sendOrderExpired(Long orderId){
        sendTemplate("order-expired", orderId);
    }

    public void sendOrderToPreparing(Long orderId){
        sendTemplate("order-to-preparing", orderId);
    }

    public void sendOrderShipment(Long orderId){
        sendTemplate("order-shipment", orderId);
    }

    public void sendOrderDelivered(Long orderId){
        sendTemplate("order-delivered", orderId);
    }
}
