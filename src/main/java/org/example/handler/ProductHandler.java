package org.example.handler;

import org.example.classes.Producto;
import org.example.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ProductHandler {
    private final ProductService service;
    public ProductHandler(ProductService service){
        this.service = service;
    }

    public Mono<ServerResponse> getAll(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Producto.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request){
        Long id = Long.parseLong(request.pathVariable("id"));
        return service.findById(id)
                .flatMap(producto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(producto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request){
        return request.bodyToMono(Producto.class)
                .flatMap(service::save)
                .flatMap(producto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(producto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest request){
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(Producto.class)
                .flatMap(producto -> service.update(id, producto))
                .flatMap(producto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(producto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> updateStock(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(Map.class)
                .flatMap(body -> {
                    Integer stock = (Integer) body.get("stock");
                    return service.updateStock(id, stock);
                })
                .flatMap(producto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(producto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getBajoStock(ServerRequest request){
        int minimo = Integer.parseInt(request.queryParam("minimo").orElse("5"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.obtenerProductosBajoStock(minimo), Map.class);
    }

    public Mono<ServerResponse> delete(ServerRequest request){
        Long id = Long.parseLong(request.pathVariable("id"));
        return service.delete(id)
                .then(ServerResponse.noContent().build());
    }
}
