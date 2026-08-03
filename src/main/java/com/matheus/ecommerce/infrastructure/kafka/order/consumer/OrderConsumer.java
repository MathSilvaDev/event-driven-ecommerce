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
        log.info("---RECEIVED_ORDER---ID: {}", orderId);
    }

    @KafkaListener(
            topics = "order-paid",
            groupId = "order-group"
    )
    public void paidConsume(Long orderId){
        log.info("---ORDER_PAID---ID: {}", orderId);
    }

    @KafkaListener(
            topics = "order-expired",
            groupId = "order-group"
    )
    public void expiredConsume(Long orderId){
        log.info("---ORDER_EXPIRED_DUE_TO_NON_PAYMENT---ID: {}", orderId);
    }

    @KafkaListener(
            topics = "order-to-preparing",
            groupId = "order-group"
    )
    public void toPreparingConsume(Long orderId){
        log.info("---ORDER_STATUS_TO_PREPARING---ID: {}", orderId);
    }

    @KafkaListener(
            topics = "order-shipment",
            groupId = "order-group"
    )
    public void shipmentConsume(Long orderId){
        log.info("---ORDER_DISPATCHED---ID: {}", orderId);
    }
}
