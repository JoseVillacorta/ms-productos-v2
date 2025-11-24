package org.example.mapper;

import org.example.classes.Producto;
import org.example.openapi.dto.Product;
import org.example.openapi.dto.ProductUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    // De DTO a entidad
    public Producto toProducto(Product dto) {
        Producto p = new Producto();
        if(dto.getId() != null) p.setId(dto.getId().longValue());
        p.setNombre(dto.getName());
        p.setDescripcion(dto.getDescription());
        if(dto.getPrice() != null) p.setPrecio(dto.getPrice().doubleValue());
        p.setStock(dto.getStock());
        p.setActivo(true); // por defecto, ya que Product no tiene 'activo'
        p.setFechaCreacion(java.time.LocalDateTime.now()); // asignar fecha actual
        return p;
    }

    // De entidad a DTO
    public Product toDto(Producto producto) {
        Product dto = new Product();
        if(producto.getId() != null) dto.setId(producto.getId().intValue());
        dto.setName(producto.getNombre());
        dto.setDescription(producto.getDescripcion());
        if(producto.getPrecio() != null) dto.setPrice(producto.getPrecio().floatValue());
        dto.setStock(producto.getStock());
        return dto;
    }

    // Actualizar entidad existente desde ProductUpdateRequest
    public Producto toProducto(ProductUpdateRequest updateRequest, Producto producto) {
        if(updateRequest.getName() != null) producto.setNombre(updateRequest.getName());
        if(updateRequest.getDescription() != null) producto.setDescripcion(updateRequest.getDescription());
        if(updateRequest.getPrice() != null) producto.setPrecio(updateRequest.getPrice().doubleValue());
        if(updateRequest.getStock() != null) producto.setStock(updateRequest.getStock());
        return producto;
    }
}
