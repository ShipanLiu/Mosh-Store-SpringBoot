#!/bin/bash

# Test Deployment Script
# This script tests the Docker setup locally before deploying to the server

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
check_docker() {
    log_info "Checking Docker installation..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed!"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        log_error "Docker is not running!"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        log_error "Docker Compose is not installed!"
        exit 1
    fi
    
    log_success "Docker is ready"
}

# Check required files
check_files() {
    log_info "Checking required files..."
    
    local required_files=(
        "Dockerfile"
        "docker-compose.yml"
        "pom.xml"
        "src/main/java/com/codewithmosh/store/StoreApplication.java"
    )
    
    for file in "${required_files[@]}"; do
        if [ ! -f "$file" ]; then
            log_error "Required file not found: $file"
            exit 1
        fi
    done
    
    log_success "All required files found"
}

# Create test environment file
create_test_env() {
    log_info "Creating test environment file..."
    
    cat > .env.test << 'EOF'
# Test Environment Configuration
DB_HOST=mysql
DB_PORT=3306
DB_NAME=cdb_mosh_p1_ecommerce_app_test
DB_USERNAME=root
DB_PASSWORD=19980223
SPRING_PROFILES_ACTIVE=docker
APP_PORT=8080
COMPOSE_PROJECT_NAME=mosh-store-test
SWAGGER_ENABLED=true
EOF
    
    log_success "Test environment file created"
}

# Build and test Docker image
test_docker_build() {
    log_info "Building Docker image..."
    
    if docker build -t mosh-store-test .; then
        log_success "Docker image built successfully"
    else
        log_error "Docker image build failed"
        exit 1
    fi
}

# Test Docker Compose
test_docker_compose() {
    log_info "Testing Docker Compose setup..."
    
    # Use test environment
    export $(grep -v '^#' .env.test | xargs)
    
    # Start services
    log_info "Starting services..."
    if docker-compose --env-file .env.test up -d; then
        log_success "Services started"
    else
        log_error "Failed to start services"
        exit 1
    fi
    
    # Wait for application to be ready
    log_info "Waiting for application to be ready..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:8080/api/v1/ > /dev/null 2>&1; then
            log_success "Application is responding!"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            log_error "Application failed to start within timeout"
            docker-compose --env-file .env.test logs app
            exit 1
        fi
        
        echo "Attempt $attempt/$max_attempts - waiting..."
        sleep 10
        ((attempt++))
    done
}

# Test API endpoints
test_api_endpoints() {
    log_info "Testing API endpoints..."
    
    # Test health endpoint
    if curl -f http://localhost:8080/api/v1/ > /dev/null 2>&1; then
        log_success "Health endpoint is working"
    else
        log_error "Health endpoint failed"
        return 1
    fi
    
    # Test API docs
    if curl -f http://localhost:8080/api-docs > /dev/null 2>&1; then
        log_success "API docs endpoint is working"
    else
        log_warning "API docs endpoint failed (might be disabled)"
    fi
    
    # Test Swagger UI (if enabled)
    if curl -f http://localhost:8080/swagger-ui.html > /dev/null 2>&1; then
        log_success "Swagger UI is working"
    else
        log_warning "Swagger UI failed (might be disabled)"
    fi
}

# Test database connection
test_database() {
    log_info "Testing database connection..."
    
    if docker-compose --env-file .env.test exec mysql mysqladmin ping -h localhost > /dev/null 2>&1; then
        log_success "Database is responding"
    else
        log_error "Database connection failed"
        return 1
    fi
    
    # Test application database connection
    if docker-compose --env-file .env.test exec app curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        log_success "Application database connection is healthy"
    else
        log_warning "Application health check failed (actuator might be disabled)"
    fi
}

# Show running containers
show_status() {
    log_info "Showing container status..."
    docker-compose --env-file .env.test ps
    
    log_info "Showing container logs (last 10 lines)..."
    docker-compose --env-file .env.test logs --tail=10
}

# Cleanup test environment
cleanup() {
    log_info "Cleaning up test environment..."
    
    # Stop and remove containers
    docker-compose --env-file .env.test down -v || true
    
    # Remove test image
    docker rmi mosh-store-test || true
    
    # Remove test environment file
    rm -f .env.test
    
    # Prune unused resources
    docker system prune -f || true
    
    log_success "Cleanup completed"
}

# Main test function
run_tests() {
    log_info "Starting deployment tests..."
    
    check_docker
    check_files
    create_test_env
    test_docker_build
    test_docker_compose
    test_api_endpoints
    test_database
    show_status
    
    log_success "All tests passed! 🎉"
    log_info "Your application is ready for deployment."
    log_info "You can access it at: http://localhost:8080/api/v1/"
    
    # Ask if user wants to keep it running
    echo ""
    read -p "Keep the test environment running? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        cleanup
    else
        log_info "Test environment is still running at http://localhost:8080"
        log_info "Run 'docker-compose --env-file .env.test down -v' to stop it"
    fi
}

# Handle script interruption
trap cleanup EXIT

# Show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help       Show this help message"
    echo "  -c, --cleanup    Only cleanup previous test runs"
    echo "  -q, --quick      Quick test (skip some checks)"
    echo ""
    echo "This script tests your Docker setup locally before deploying to the server."
}

# Main execution
case "${1:-}" in
    -h|--help)
        show_usage
        exit 0
        ;;
    -c|--cleanup)
        cleanup
        exit 0
        ;;
    -q|--quick)
        log_info "Running quick tests..."
        check_docker
        check_files
        test_docker_build
        log_success "Quick tests passed!"
        ;;
    "")
        run_tests
        ;;
    *)
        log_error "Unknown option: $1"
        show_usage
        exit 1
        ;;
esac 