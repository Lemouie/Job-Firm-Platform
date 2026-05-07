#!/usr/bin/env bash
# ============================================
# Job-Firm Platform - Startup Script
# ============================================
# This script coordinates startup of all services.
#
# Usage:
#   ./scripts/startup.sh              - Full startup with build
#   ./scripts/startup.sh --env-only   - Start only infrastructure services
#   ./scripts/startup.sh --no-build   - Start without rebuilding images
#   ./scripts/startup.sh --help       - Show help
# ============================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

log_info()  { echo -e "${CYAN}[INFO]${NC}  $*"; }
log_ok()    { echo -e "${GREEN}[OK]${NC}    $*"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
log_error() { echo -e "${RED}[ERROR]${NC} $*"; }

# Default values
ENV_ONLY=false
NO_BUILD=false
COMPOSE_FILE="docker-compose.yml"

# Parse arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        --env-only)
            ENV_ONLY=true
            COMPOSE_FILE="docker-compose.env.yml"
            shift
            ;;
        --no-build)
            NO_BUILD=true
            shift
            ;;
        --help)
            echo "Job-Firm Platform Startup Script"
            echo ""
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --env-only     Start only infrastructure services (MySQL, Redis, Nacos, RocketMQ)"
            echo "  --no-build     Start without rebuilding Docker images"
            echo "  --help         Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                     # Full startup with build"
            echo "  $0 --env-only          # Start infrastructure only"
            echo "  $0 --no-build          # Start without rebuilding"
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            echo "Usage: $0 [--env-only] [--no-build] [--help]"
            exit 1
            ;;
    esac
done

cd "$PROJECT_DIR"

echo ""
echo "========================================="
echo "  Job-Firm Platform 启动脚本"
echo "========================================="
echo ""

# ============================================
# Check prerequisites
# ============================================
log_info "Checking prerequisites..."

if ! command -v docker &>/dev/null; then
    log_error "Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker compose &>/dev/null; then
    log_error "Docker Compose is not installed. Please install Docker Compose V2 first."
    exit 1
fi

log_ok "Docker and Docker Compose are available"

# ============================================
# Check for required files
# ============================================
if [[ ! -f "$PROJECT_DIR/sql/init-all-databases.sql" ]]; then
    log_error "Database initialization script not found: sql/init-all-databases.sql"
    exit 1
fi

if [[ ! -f "$PROJECT_DIR/$COMPOSE_FILE" ]]; then
    log_error "Docker Compose file not found: $COMPOSE_FILE"
    exit 1
fi

log_ok "All required files found"

# ============================================
# Build Docker images (unless --no-build or --env-only)
# ============================================
if [[ "$ENV_ONLY" == "false" ]] && [[ "$NO_BUILD" == "false" ]]; then
    echo ""
    log_info "Building microservice Docker images..."
    echo ""

    MODULES=(
        "job-firm-gateway"
        "job-firm-auth"
        "job-firm-user-service"
        "job-firm-firm-service"
        "job-firm-job-service"
        "job-firm-order-service"
        "job-firm-payment-service"
        "job-firm-recommend-search-service"
        "job-firm-admin-service"
        "job-firm-archive-service"
    )

    for MODULE in "${MODULES[@]}"; do
        log_info "Building image for $MODULE..."

        # Check if the module directory exists
        if [[ ! -d "$PROJECT_DIR/$MODULE" ]]; then
            log_warn "Module directory not found: $MODULE — skipping build"
            continue
        fi

        if docker build \
            --build-arg MODULE_NAME="$MODULE" \
            -t "job-firm-platform/$MODULE:latest" \
            -f "$PROJECT_DIR/Dockerfile" \
            "$PROJECT_DIR"; then
            log_ok "Successfully built $MODULE"
        else
            log_error "Failed to build $MODULE"
            exit 1
        fi
    done

    log_ok "All microservice images built successfully"
fi

# ============================================
# Start infrastructure services
# ============================================
echo ""
log_info "Starting infrastructure services (MySQL, Redis, Nacos, RocketMQ)..."
echo ""

docker compose -f "$PROJECT_DIR/$COMPOSE_FILE" up -d mysql redis nacos rocketmq-namesrv rocketmq-broker

echo ""

# ============================================
# Wait for infrastructure readiness
# ============================================
log_info "Waiting for MySQL to be ready..."
"$SCRIPT_DIR/wait-for-it.sh" mysql:3306 -t 120 -q
log_ok "MySQL is ready"

log_info "Waiting for Redis to be ready..."
"$SCRIPT_DIR/wait-for-it.sh" redis:6379 -t 30 -q
log_ok "Redis is ready"

log_info "Waiting for Nacos to be ready..."
"$SCRIPT_DIR/wait-for-it.sh" nacos:8848 -t 180 -q
log_ok "Nacos is ready"

log_info "Waiting for RocketMQ NameServer to be ready..."
"$SCRIPT_DIR/wait-for-it.sh" rocketmq-namesrv:9876 -t 60 -q
log_ok "RocketMQ NameServer is ready"

# Wait a bit for the broker to register with the NameServer
log_info "Waiting for RocketMQ Broker to register..."
sleep 5
log_ok "RocketMQ Broker should be ready"

# ============================================
# Start microservices (only if not --env-only)
# ============================================
if [[ "$ENV_ONLY" == "false" ]]; then
    echo ""
    log_info "Starting microservices..."

    # Start gateway and auth first (no DB dependency)
    log_info "Starting gateway and auth services..."
    docker compose -f "$PROJECT_DIR/$COMPOSE_FILE" up -d job-firm-gateway job-firm-auth

    "$SCRIPT_DIR/wait-for-it.sh" localhost:8080 -t 120 -q || log_warn "Gateway health check timeout (continuing anyway)"
    "$SCRIPT_DIR/wait-for-it.sh" localhost:8081 -t 120 -q || log_warn "Auth service health check timeout (continuing anyway)"
    log_ok "Gateway and Auth services are ready"

    # Start DB-backed services (user, firm, job)
    log_info "Starting user, firm, and job services..."
    docker compose -f "$PROJECT_DIR/$COMPOSE_FILE" up -d \
        job-firm-user-service \
        job-firm-firm-service \
        job-firm-job-service

    "$SCRIPT_DIR/wait-for-it.sh" localhost:8082 -t 120 -q || log_warn "User service health check timeout"
    "$SCRIPT_DIR/wait-for-it.sh" localhost:8083 -t 120 -q || log_warn "Firm service health check timeout"
    "$SCRIPT_DIR/wait-for-it.sh" localhost:8084 -t 120 -q || log_warn "Job service health check timeout"
    log_ok "User, Firm, and Job services are ready"

    # Start remaining services (order, payment, recommend, admin, archive)
    log_info "Starting remaining services..."
    docker compose -f "$PROJECT_DIR/$COMPOSE_FILE" up -d \
        job-firm-order-service \
        job-firm-payment-service \
        job-firm-recommend-search-service \
        job-firm-admin-service \
        job-firm-archive-service

    log_ok "All services started"
fi

# ============================================
# Summary
# ============================================
echo ""
echo "========================================="
echo "  Deployment Status Summary"
echo "========================================="
echo ""

docker compose -f "$PROJECT_DIR/$COMPOSE_FILE" ps

echo ""
echo "========================================="
log_ok "Job-Firm Platform is deployed!"
echo ""
echo "  Gateway API:      http://localhost:8080"
echo "  Nacos Console:    http://localhost:8848/nacos"
echo "  MySQL:            localhost:3306 (root/root123)"
echo "  Redis:            localhost:6379"
echo "  RocketMQ:         localhost:9876"
echo "========================================="
echo ""
