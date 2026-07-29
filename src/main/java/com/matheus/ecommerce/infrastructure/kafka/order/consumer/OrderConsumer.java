package com.matheus.ecommerce.infrastructure.kafka.order.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderConsumer {

    @KafkaListener(
            topics = "order-created",
            groupId = "order-group"
    )
    public void createdConsume(String orderId){
        log.info("---RECEIVED-ORDER---: {}", orderId);
    }

    @KafkaListener(
            topics = "order-paid",
            groupId = "order-group"
    )
    public void paidConsume(String orderId){
        log.info("---ORDER-PAID---: {}", orderId);
    }
}
