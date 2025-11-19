package org.example.producer;

import org.example.events.ProductCreatedEvent;
import org.example.events.ProductDeletedEvent;
import org.example.events.ProductStockUpdatedEvent;
import org.example.events.ProductUpdatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String CREATED_TOPIC = "product-created";
    private static final String UPDATED_TOPIC = "product-updated";
    private static final String DELETED_TOPIC = "product-deleted";
    private static final String STOCK_UPDATED_TOPIC = "product-stock-updated";

    public ProductProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductCreatedEvent(ProductCreatedEvent event) {
        kafkaTemplate.send(CREATED_TOPIC, event);
    }

    public void sendProductUpdatedEvent(ProductUpdatedEvent event) {
        kafkaTemplate.send(UPDATED_TOPIC, event);
    }

    public void sendProductDeletedEvent(ProductDeletedEvent event) {
        kafkaTemplate.send(DELETED_TOPIC, event);
    }

    public void sendProductStockUpdatedEvent(ProductStockUpdatedEvent event) {
        kafkaTemplate.send(STOCK_UPDATED_TOPIC, event);
    }
}
