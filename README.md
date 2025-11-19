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

### Endpoints de Monitoreo

#### Health Check
```bash
curl http://localhost:8083/actuator/health
```

#### Métricas
```bash
curl http://localhost:8083/actuator/prometheus
```

#### Info del Servicio
```bash
curl http://localhost:8083/actuator/info
```

## Ejemplos Completos de Uso

### 1. Crear Catálogo Completo

```bash
# Producto 1 - Laptop
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "MacBook Pro M3",
    "descripcion": "Laptop profesional con chip M3",
    "precio": 2499.99,
    "stock": 15
  }'

# Producto 2 - Mouse
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Mouse Inalámbrico Logitech",
    "descripcion": "Mouse ergonómico con batería larga duración",
    "precio": 49.99,
    "stock": 3
  }'

# Producto 3 - Teclado
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Teclado Mecánico RGB",
    "descripcion": "Teclado para gaming con retroiluminación",
    "precio": 129.99,
    "stock": 8
  }'
```

### 2. Consultar Inventario

```bash
# Ver todos los productos
curl http://localhost:8080/api/products

# Ver productos bajo stock
curl "http://localhost:8080/api/products/bajo-stock?minimo=5"

# Ver producto específico
curl http://localhost:8080/api/products/1
```

### 3. Gestión de Stock

```bash
# Reponer stock de mouse (estaba en 3)
curl -X PUT http://localhost:8080/api/products/2/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 25}'

# Verificar productos bajo stock nuevamente
curl "http://localhost:8080/api/products/bajo-stock?minimo=5"
```

## Eventos Kafka

El microservicio publica automáticamente eventos a los siguientes topics:

### product-created
```json
{
  "productId": 1,
  "nombre": "Producto creado",
  "precio": 100.0,
  "stock": 10,
  "eventType": "PRODUCT_CREATED",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

### product-updated
```json
{
  "productId": 1,
  "nombre": "Producto actualizado",
  "precio": 120.0,
  "stock": 15,
  "eventType": "PRODUCT_UPDATED",
  "timestamp": "2025-01-15T10:35:00Z"
}
```

### product-stock-updated
```json
{
  "productId": 1,
  "stockAnterior": 10,
  "stockNuevo": 50,
  "eventType": "PRODUCT_STOCK_UPDATED",
  "timestamp": "2025-01-15T10:40:00Z"
}
```

### product-deleted
```json
{
  "productId": 1,
  "eventType": "PRODUCT_DELETED",
  "timestamp": "2025-01-15T10:45:00Z"
}
```

### Consumir Eventos
```bash
# Consumir eventos de productos creados
docker-compose exec kafka kafka-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic product-created \
  --from-beginning

# Ver todos los topics
docker-compose exec kafka kafka-topics --list \
  --bootstrap-server kafka:9092
```

## Configuración por Entornos

### Perfiles Soportados
- **dev**: Desarrollo local (puerto 8083)
- **qa**: Testing/QA (puerto 8083)
- **prod**: Producción (puerto 8083)

### Configuración Centralizada
El microservicio obtiene su configuración desde:
- **Config Server**: http://localhost:8888
- **Archivo**: `ms-productos-v2-dev.yaml`, `ms-productos-v2-qa.yaml`, `ms-productos-v2-prod.yaml`
- **Repositorio**: `config-repo/`

## Verificación y Testing

### Health Check
```bash
curl http://localhost:8083/actuator/health
```
**Respuesta esperada**: `{"status":"UP"}`

### Test de Carga
```bash
# Crear múltiples productos para test
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/products \
    -H "Content-Type: application/json" \
    -d "{\"nombre\":\"Producto Test $i\",\"descripcion\":\"Test\",\"precio\":$((10 + i)),\"stock\":$((5 + i))}"
done

# Verificar todos los productos
curl http://localhost:8080/api/products | jq '.'
```

## Troubleshooting

### Base de Datos No Conecta
```bash
# Verificar estado de PostgreSQL
docker-compose logs -f db_productos

# Verificar conectividad
curl -v http://localhost:8083/actuator/health
```

### Kafka No Funciona
```bash
# Verificar Kafka
docker-compose logs -f kafka

# Verificar topics
docker-compose exec kafka kafka-topics --list --bootstrap-server kafka:9092
```

### Registry Service No Disponible
```bash
# Verificar Eureka
docker-compose logs -f registry-service

# Verificar registro del servicio
curl http://localhost:8761/eureka/apps
```

### Puerto En Uso
```bash
# Verificar puerto
netstat -tulpn | grep :8083

# Cambiar puerto en configuración
# Editar config-repo/ms-productos-v2-dev.yaml
```

## Integración

### Con MS Pedidos
MS Productos V2 provee datos para:
- **Validación de Stock**: MS Pedidos consulta disponibilidad
- **Información de Productos**: Detalles para cálculo de precios
- **Eventos**: Notificaciones de cambios en inventario

### Con Gateway Service
- **Ruta**: `/api/products/**` → MS Productos V2
- **Load Balancing**: Balanceo automático entre instancias
- **Fallback**: Manejo de errores con circuit breakers

### Con Config Server
- **URL**: http://localhost:8888/ms-productos-v2/dev
- **Configuración**: `config-repo/ms-productos-v2-dev.yaml`
- **Auto Refresh**: Actualización automática de configuración


