# MS Productos V2

Microservicio reactivo para gestión de productos con arquitectura event-driven y streaming de eventos.


- **Arquitectura Reactiva**: Programación reactiva con Spring WebFlux para alta concurrencia
- **Event-Driven**: Publica eventos automáticamente a Kafka para comunicación asíncrona
- **Base de Datos Reactiva**: R2DBC con PostgreSQL para operaciones no bloqueantes
- **Service Discovery**: Registro automático en Eureka para descubrimiento dinámico
- **Configuración Centralizada**: Configuración via Spring Cloud Config Server

## Funcionalidades

### CRUD de Productos
- **Crear**: Registro de nuevos productos con validación
- **Leer**: Consulta de productos por ID y listado completo
- **Actualizar**: Modificación de datos de productos existentes
- **Eliminar**: Eliminación lógica y física de productos

### Gestión de Inventario
- **Control de Stock**: Actualización y consulta de inventario
- **Alertas de Stock Bajo**: Endpoint específico para productos con stock mínimo
- **Historial de Cambios**: Registro de todas las modificaciones de stock

### Event-Driven Architecture
- **Eventos Kafka**: Publicación automática de eventos para:
  - Productos creados (`product-created`)
  - Productos actualizados (`product-updated`)
  - Productos eliminados (`product-deleted`)
  - Stock actualizado (`product-stock-updated`)

## Dependencias

### Base de Datos
- **PostgreSQL**: Base de datos reactiva con R2DBC
- **Base de datos**: `db_productos_dev` (dev), `db_productos_qa` (qa), `db_productos_prod` (prod)
- **Script**: Ejecutar `database/script.sql` para inicialización

### Servicios de Infraestructura
- **Registry Service**: http://localhost:8761 (Eureka)
- **Config Server**: http://localhost:8888 (Spring Cloud Config)
- **Kafka**: Event streaming platform en puerto 9092
- **Gateway**: API Gateway en puerto 8080

## Ejecutar

### Desarrollo Local
```bash
./gradlew bootRun --spring.profiles.active=dev
```

### Docker
```bash
docker-compose up --build ms-productos-v2
```

**Puerto**: 8083
**Actuator**: http://localhost:8083/actuator

## Endpoints Disponibles

### API REST - Productos

#### GET /api/products
Obtener todos los productos
```bash
curl http://localhost:8080/api/products
```

#### GET /api/products/{id}
Obtener producto por ID
```bash
curl http://localhost:8080/api/products/1
```

#### POST /api/products
Crear nuevo producto
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Dell XPS 13",
    "descripcion": "Laptop ultraliviana con procesador Intel Core i7",
    "precio": 1299.99,
    "stock": 25
  }'
```

#### PUT /api/products/{id}
Actualizar producto existente
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Dell XPS 13 Pro",
    "descripcion": "Versión mejorada con más RAM",
    "precio": 1499.99,
    "stock": 20
  }'
```

#### PUT /api/products/{id}/stock
Actualizar stock del producto
```bash
curl -X PUT http://localhost:8080/api/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 50}'
```

#### GET /api/products/bajo-stock
Obtener productos con stock bajo (por defecto: stock ≤ 5)
```bash
# Stock menor o igual a 5 (por defecto)
curl "http://localhost:8080/api/products/bajo-stock"

# Stock menor o igual a 10
curl "http://localhost:8080/api/products/bajo-stock?minimo=10"
```

#### DELETE /api/products/{id}
Eliminar producto
```bash
curl -X DELETE http://localhost:8080/api/products/1
```
