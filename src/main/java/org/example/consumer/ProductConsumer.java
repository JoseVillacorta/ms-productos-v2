package org.example.consumer;

import org.example.events.ProductCreatedEvent;
import org.example.events.ProductDeletedEvent;
import org.example.events.ProductStockUpdatedEvent;
import org.example.events.ProductUpdatedEvent;
import org.example.classes.Producto;
import org.example.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductConsumer {

    private final ProductRepository productRepository;

    public ProductConsumer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @KafkaListener(topics = "product-created", groupId = "product-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeProductCreatedEvent(ProductCreatedEvent event) {
        Producto producto = new Producto();
        producto.setId(event.getId());
        producto.setNombre(event.getNombre());
        producto.setDescripcion(event.getDescripcion());
        producto.setPrecio(event.getPrecio());
        producto.setStock(event.getStock());
        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());
        productRepository.save(producto).subscribe();
    }

    @KafkaListener(topics = "product-updated", groupId = "product-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeProductUpdatedEvent(ProductUpdatedEvent event) {
        productRepository.findById(event.getId())
                .flatMap(existing -> {
                    existing.setNombre(event.getNombre());
                    existing.setDescripcion(event.getDescripcion());
                    existing.setPrecio(event.getPrecio());
                    existing.setStock(event.getStock());
                    return productRepository.save(existing);
                })
                .subscribe();
    }

    @KafkaListener(topics = "product-deleted", groupId = "product-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeProductDeletedEvent(ProductDeletedEvent event) {
        productRepository.deleteById(event.getId()).subscribe();
    }

    @KafkaListener(topics = "product-stock-updated", groupId = "product-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeProductStockUpdatedEvent(ProductStockUpdatedEvent event) {
        productRepository.findById(event.getId())
                .flatMap(existing -> {
                    existing.setStock(event.getStock());
                    return productRepository.save(existing);
                })
                .subscribe();
    }
}
