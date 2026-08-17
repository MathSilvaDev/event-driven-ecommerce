package com.matheus.ecommerce.infrastructure.kafka.order.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.matheus.ecommerce.infrastructure.kafka.order.dto.OrderKafkaResponse;

@Service
@Slf4j
public class OrderConsumer {

    @KafkaListener(
            topics = "order-status",
            groupId = "order-group"
    )
    public void reciveOrderStatus(OrderKafkaResponse response){
        log.info("---ORDER-STATUS---: {}", response);
    }
}
