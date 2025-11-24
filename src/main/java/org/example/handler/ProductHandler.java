package org.example.handler;

import org.example.classes.Producto;
import org.example.mapper.ProductMapper;
import org.example.openapi.dto.Product;
import org.example.openapi.dto.ProductUpdateRequest;
import org.example.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ProductHandler {

    private final ProductService service;
    private final ProductMapper mapper;

    public ProductHandler(ProductService service, ProductMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // Obtener todos los productos
    public Mono<ServerResponse> getAll(ServerRequest request) {
        Flux<Producto> productos = service.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productos.map(mapper::toDto), Product.class);
    }

    // Obtener producto por ID
    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return service.findById(id)
                .map(mapper::toDto)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    // Crear nuevo producto
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .map(mapper::toProducto)
                .flatMap(service::save)
                .map(mapper::toDto)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
    }

    // Actualizar producto completo
    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(ProductUpdateRequest.class)
                .flatMap(updateRequest ->
                        service.findById(id)
                                .flatMap(existing -> service.update(id, mapper.toProducto(updateRequest, existing)))
                                .map(mapper::toDto)
                )
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    // Actualizar solo stock
    public Mono<ServerResponse> updateStock(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(Map.class)
                .flatMap(body -> {
                    Integer stock = (Integer) body.get("stock");
                    return service.updateStock(id, stock)
                            .map(mapper::toDto);
                })
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    // Obtener productos bajo stock
    public Mono<ServerResponse> getBajoStock(ServerRequest request) {
        int minimo = Integer.parseInt(request.queryParam("minimo").orElse("5"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.obtenerProductosBajoStock(minimo).map(mapper::toDto), Product.class);
    }

    // Eliminar producto
    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return service.delete(id)
                .then(ServerResponse.noContent().build());
    }
}
