package org.example.router;
import org.example.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> route(ProductHandler handler){
        return RouterFunctions
                .route()
                .GET("/api/productos", handler::getAll)
                .GET("/api/productos/bajo-stock", handler::getBajoStock)  // Mover arriba
                .GET("/api/productos/{id}", handler::getById)  // Después
                .POST("/api/productos", handler::create)
                .PUT("/api/productos/{id}", handler::update)
                .PUT("/api/productos/{id}/stock", handler::updateStock)
                .DELETE("/api/productos/{id}", handler::delete)
                .build();
    }
}
