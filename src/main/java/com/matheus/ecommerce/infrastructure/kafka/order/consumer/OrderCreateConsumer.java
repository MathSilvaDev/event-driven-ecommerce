package com.matheus.ecommerce.infrastructure.kafka.order.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderCreateConsumer {

    @KafkaListener(
            topics = "order-created",
            groupId = "order-group"
    )
    public void consume(String orderId){
        log.info("---RECEIVED-ORDER---: {}", orderId);
    }
}
