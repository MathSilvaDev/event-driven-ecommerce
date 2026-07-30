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
    public void createdConsume(Long orderId){
        log.info("---RECEIVED-ORDER---: {}", orderId);
    }

    @KafkaListener(
            topics = "order-paid",
            groupId = "order-group"
    )
    public void paidConsume(Long orderId){
        log.info("---ORDER-PAID---: {}", orderId);
    }

    @KafkaListener(
            topics = "order-to-preparing",
            groupId = "order-group"
    )
    public void toPreparingConsume(Long orderId){
        log.info("---ORDER-STATUS-TO-PREPARING---: {}", orderId);
    }
}
