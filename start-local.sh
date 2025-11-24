#!/bin/bash

# Script para ejecutar ms-productos-v2 en Docker Compose
# Uso: ./start-local.sh [comando]

set -euo pipefail

COMPOSE_FILE="docker-compose.yml"
PROJECT_NAME="ms-productos-v2"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log() {
    printf '[%s] %s\n' "$(date '+%H:%M:%S')" "$*"
}

error() {
    printf '\033[0;31mError: %s\033[0m\n' "$*" >&2
    exit 1
}

success() {
    printf '\033[0;32m%s\033[0m\n' "$*"
}

usage() {
    cat <<EOF
Uso: $0 [comando]

Comandos disponibles:
  up       Levanta todos los servicios
  down     Detiene todos los servicios  
  build    Construye las imágenes
  logs     Muestra los logs de ms-productos-v2
  status   Muestra el estado de los contenedores
  test     Ejecuta health checks
  clean    Limpia volúmenes y contenedores

Ejemplos:
  $0 up        # Inicia todos los servicios
  $0 logs      # Ve los logs del microservicio
  $0 down      # Detiene todos los servicios
  $0 clean     # Limpia todo y empieza de cero
EOF
}

check_requirements() {
    if ! command -v docker >/dev/null 2>&1; then
        error "Docker no está instalado o no está en el PATH"
    fi
    
    if ! docker compose version >/dev/null 2>&1; then
        error "Docker Compose no está disponible. Instala Docker Compose v2"
    fi
}

start_services() {
    log "Iniciando servicios de ms-productos-v2..."
    docker compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up -d
    
    success "Servicios iniciados correctamente"
    echo ""
    log "URLs de acceso:"
    echo "  - Microservicio: http://localhost:8083"
    echo "  - Health Check: http://localhost:8083/actuator/health"
    echo ""
    log "Para ver los logs: $0 logs"
    log "Para detener: $0 down"
}

stop_services() {
    log "Deteniendo servicios..."
    docker compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" down
    success "Servicios detenidos"
}

build_images() {
    log "Construyendo imágenes..."
    docker compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" build
    success "Imágenes construidas correctamente"
}

show_logs() {
    if [[ $# -eq 0 ]]; then
        log "Mostrando logs de ms-productos-v2..."
        docker compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" logs -f ms-productos-v2
    else
        log "Mostrando logs de $1..."
        docker compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" logs -f "$1"
    fi
}

show_status() {
    log "Estado de los contenedores:"
    docker compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" ps
}

run_health_checks() {
    log "Ejecutando health checks..."
    
    # Esperar a que el servicio esté disponible
    log "Esperando a que ms-productos-v2 esté disponible..."
    sleep 30
    
    # Health check
    if curl -f -s http://localhost:8083/actuator/health >/dev/null; then
        success "✓ ms-productos-v2 está respondiendo correctamente"
    else
        error "✗ ms-productos-v2 no está respondiendo"
    fi
    
    # Verificar que Kafka esté disponible
    if docker exec -i "$PROJECT_NAME-kafka-1" kafka-topics --bootstrap-server localhost:9092 --list >/dev/null 2>&1; then
        success "✓ Kafka está funcionando correctamente"
    else
        error "✗ Kafka no está funcionando"
    fi
    
    # Verificar PostgreSQL
    if docker exec -i "$PROJECT_NAME-postgres-productos-1" pg_isready -U postgres >/dev/null 2>&1; then
        success "✓ PostgreSQL está funcionando correctamente"
    else
        error "✗ PostgreSQL no está funcionando"
    fi
}

clean_all() {
    log "Limpiando contenedores, imágenes y volúmenes..."
    docker compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" down -v --remove-orphans
    docker image prune -f
    docker system prune -f
    success "Limpieza completada"
}

main() {
    local cmd="${1:-}"
    
    case "$cmd" in
        up)
            check_requirements
            start_services
            ;;
        down)
            check_requirements
            stop_services
            ;;
        build)
            check_requirements
            build_images
            ;;
        logs)
            check_requirements
            show_logs "${2:-}"
            ;;
        status)
            check_requirements
            show_status
            ;;
        test)
            check_requirements
            run_health_checks
            ;;
        clean)
            check_requirements
            clean_all
            ;;
        help|--help|-h|"")
            usage
            ;;
        *)
            error "Comando desconocido: $cmd"
            ;;
    esac
}

main "$@"