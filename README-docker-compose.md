# Docker Compose Local para ms-productos-v2

Esta configuración te permite ejecutar el microservicio ms-productos-v2 localmente usando Docker Compose, replicando el comportamiento de Kubernetes.

## Servicios Incluidos

- **postgres-productos**: Base de datos PostgreSQL con la configuración de desarrollo
- **zookeeper**: Coordinación para Kafka
- **kafka**: Broker de mensajes para eventos
- **ms-productos-v2**: El microservicio principal

## Prerrequisitos

- Docker y Docker Compose v2 instalados
- Puerto 8083 disponible (microservicio)
- Puerto 5432 disponible (PostgreSQL)
- Puerto 9092 disponible (Kafka)
- Puerto 2181 disponible (Zookeeper)

## Uso Rápido

### Iniciar todos los servicios

```bash
chmod +x start-local.sh
./start-local.sh up
```

### Ver logs del microservicio

```bash
./start-local.sh logs
```

### Detener todos los servicios

```bash
./start-local.sh down
```

## Comandos Disponibles

| Comando | Descripción |
|---------|-------------|
| `./start-local.sh up` | Levanta todos los servicios |
| `./start-local.sh down` | Detiene todos los servicios |
| `./start-local.sh build` | Construye las imágenes Docker |
| `./start-local.sh logs` | Muestra los logs del microservicio |
| `./start-local.sh status` | Muestra el estado de los contenedores |
| `./start-local.sh test` | Ejecuta health checks |
| `./start-local.sh clean` | Limpia contenedores y volúmenes |

## Configuración

### Variables de Entorno

El microservicio está configurado con las siguientes variables:

```yaml
SPRING_PROFILES_ACTIVE: dev
SPRING_R2DBC_URL: r2dbc:postgresql://postgres-productos:5432/db_productos_dev
SPRING_R2DBC_USERNAME: postgres
SPRING_R2DBC_PASSWORD: admin
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
SERVER_PORT: 8083
```
