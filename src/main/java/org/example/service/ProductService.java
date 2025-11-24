package org.example.service;

import org.example.classes.Producto;
import org.example.events.*;
import org.example.producer.ProductProducer;
import org.example.repository.ProductRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final DatabaseClient databaseClient;
    private final ProductProducer productProducer;

    public ProductService(ProductRepository repository, DatabaseClient databaseClient, ProductProducer productProducer) {
        this.repository = repository;
        this.databaseClient = databaseClient;
        this.productProducer = productProducer;
    }

    public Flux<Producto> findAll() {
        return repository.findAll();
    }

    public Mono<Producto> findById(Long id) {
        return repository.findById(id);
    }

    public Flux<Producto> obtenerProductosBajoStock(Integer minimo) {
        return databaseClient.sql("SELECT * FROM productos_bajo_stock($1)")
                .bind(0, minimo)
                .map(row -> {
                    Producto p = new Producto();
                    p.setId(row.get("id", Long.class));
                    p.setNombre(row.get("nombre", String.class));
                    p.setDescripcion(row.get("descripcion", String.class));
                    p.setPrecio(row.get("precio", Double.class));
                    p.setStock(row.get("stock", Integer.class));
                    p.setActivo(row.get("activo", Boolean.class));
                    p.setFechaCreacion(row.get("fecha_creacion", java.time.LocalDateTime.class));
                    return p;
                })
                .all();
    }


    // Commands (escrituras asíncronas)
    public Mono<Producto> save(Producto producto) {
        ProductCreatedEvent event = new ProductCreatedEvent(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock()
        );
        productProducer.sendProductCreatedEvent(event);
        return Mono.just(producto); // Respuesta inmediata, no espera DB
    }

    public Mono<Producto> update(Long id, Producto updated) {
        ProductUpdatedEvent event = new ProductUpdatedEvent(
                id,
                updated.getNombre(),
                updated.getDescripcion(),
                updated.getPrecio(),
                updated.getStock()
        );
        productProducer.sendProductUpdatedEvent(event);
        return Mono.just(updated);
    }

    public Mono<Producto> updateStock(Long id, Integer newStock) {
        ProductStockUpdatedEvent event = new ProductStockUpdatedEvent(id, newStock);
        productProducer.sendProductStockUpdatedEvent(event);
        return repository.findById(id)
                .map(producto -> {
                    producto.setStock(newStock);
                    return producto;
                });
    }

    public Mono<Void> delete(Long id) {
        ProductDeletedEvent event = new ProductDeletedEvent(id);
        productProducer.sendProductDeletedEvent(event);
        return Mono.empty();
    }

}
