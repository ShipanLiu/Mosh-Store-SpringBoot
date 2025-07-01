#!/bin/bash

# Mosh Store Deployment Script
# This script deploys the application to the Ubuntu server

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SERVER_HOST="${SERVER_HOST:-100.82.121.103}"
SERVER_USER="${SERVER_USER:-shipan}"
SSH_KEY="${SSH_KEY:-~/.ssh/id_ed25519}"
APP_NAME="mosh-store"
DEPLOY_DIR="/opt/${APP_NAME}"
DOCKER_COMPOSE_FILE="docker-compose.yml"

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

# Check if required files exist
check_requirements() {
    log_info "Checking requirements..."
    
    if [ ! -f "$DOCKER_COMPOSE_FILE" ]; then
        log_error "docker-compose.yml not found!"
        exit 1
    fi
    
    if [ ! -f "$SSH_KEY" ]; then
        log_error "SSH key not found at $SSH_KEY"
        exit 1
    fi
    
    log_success "Requirements check passed"
}

# Test SSH connection
test_ssh() {
    log_info "Testing SSH connection to $SERVER_USER@$SERVER_HOST..."
    
    if ssh -i "$SSH_KEY" -o ConnectTimeout=10 -o BatchMode=yes "$SERVER_USER@$SERVER_HOST" echo "SSH connection successful" > /dev/null 2>&1; then
        log_success "SSH connection established"
    else
        log_error "SSH connection failed!"
        log_error "Please check:"
        log_error "1. Server is accessible: $SERVER_HOST"
        log_error "2. SSH key is correct: $SSH_KEY"
        log_error "3. Username is correct: $SERVER_USER"
        exit 1
    fi
}

# Setup deployment directory on server
setup_server() {
    log_info "Setting up deployment directory on server..."
    
    ssh -i "$SSH_KEY" "$SERVER_USER@$SERVER_HOST" << 'EOF'
        # Create deployment directory
        sudo mkdir -p /opt/mosh-store
        sudo chown $USER:$USER /opt/mosh-store
        
        # Install Docker if not present
        if ! command -v docker &> /dev/null; then
            echo "Installing Docker..."
            curl -fsSL https://get.docker.com -o get-docker.sh
            sudo sh get-docker.sh
            sudo usermod -aG docker $USER
            rm get-docker.sh
        fi
        
        # Install Docker Compose if not present
        if ! command -v docker-compose &> /dev/null; then
            echo "Installing Docker Compose..."
            sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
            sudo chmod +x /usr/local/bin/docker-compose
        fi
        
        echo "Server setup completed"
EOF
    
    log_success "Server setup completed"
}

# Deploy application
deploy_app() {
    log_info "Deploying application..."
    
    # Copy files to server
    log_info "Copying deployment files..."
    scp -i "$SSH_KEY" -r docker-compose.yml docker/ "$SERVER_USER@$SERVER_HOST:$DEPLOY_DIR/" 2>/dev/null || true
    
    # Create environment file
    log_info "Creating environment configuration..."
    ssh -i "$SSH_KEY" "$SERVER_USER@$SERVER_HOST" << EOF
        cd $DEPLOY_DIR
        
        # Create .env file if it doesn't exist
        if [ ! -f .env ]; then
            cat > .env << 'ENVEOF'
DB_HOST=mysql
DB_PORT=3306
DB_NAME=cdb_mosh_p1_ecommerce_app_prod
DB_USERNAME=moshuser
DB_PASSWORD=secure_password_change_me
SPRING_PROFILES_ACTIVE=prod
APP_PORT=8080
NGINX_PORT=80
NGINX_SSL_PORT=443
COMPOSE_PROJECT_NAME=mosh-store
ENVEOF
            echo "Created default .env file. Please update with your actual values!"
        fi
EOF
    
    # Deploy with Docker Compose
    log_info "Starting application with Docker Compose..."
    ssh -i "$SSH_KEY" "$SERVER_USER@$SERVER_HOST" << EOF
        cd $DEPLOY_DIR
        
        # Stop existing containers
        docker-compose down || true
        
        # Pull latest images (if using pre-built images)
        # docker-compose pull || true
        
        # Build and start containers
        docker-compose up -d --build
        
        # Wait for application to be ready
        echo "Waiting for application to start..."
        for i in {1..30}; do
            if curl -f http://localhost:8080/api/v1/ > /dev/null 2>&1; then
                echo "Application is healthy!"
                break
            fi
            echo "Waiting for application... (\$i/30)"
            sleep 10
        done
        
        # Show running containers
        echo "Running containers:"
        docker-compose ps
EOF
    
    log_success "Application deployed successfully"
}

# Verify deployment
verify_deployment() {
    log_info "Verifying deployment..."
    
    ssh -i "$SSH_KEY" "$SERVER_USER@$SERVER_HOST" << 'EOF'
        cd /opt/mosh-store
        
        # Check if containers are running
        if [ $(docker-compose ps -q | wc -l) -eq 0 ]; then
            echo "ERROR: No containers are running!"
            exit 1
        fi
        
        # Check application health
        if curl -f http://localhost:8080/api/v1/ > /dev/null 2>&1; then
            echo "✅ Application is healthy and responding"
        else
            echo "❌ Application health check failed"
            echo "Container logs:"
            docker-compose logs --tail=20 app
            exit 1
        fi
        
        # Show system resources
        echo "System resources:"
        docker stats --no-stream
EOF
    
    log_success "Deployment verification completed"
}

# Show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help           Show this help message"
    echo "  -s, --setup-only     Only setup server (install Docker, etc.)"
    echo "  -d, --deploy-only    Only deploy application (skip setup)"
    echo "  -v, --verify-only    Only verify deployment"
    echo ""
    echo "Environment variables:"
    echo "  SERVER_HOST          Server hostname or IP (default: 100.82.121.103)"
    echo "  SERVER_USER          SSH username (default: shipan)"
    echo "  SSH_KEY              Path to SSH private key (default: ~/.ssh/id_ed25519)"
    echo ""
    echo "Examples:"
    echo "  $0                   Full deployment (setup + deploy + verify)"
    echo "  $0 --setup-only      Only setup server"
    echo "  $0 --deploy-only     Only deploy application"
    echo "  SERVER_HOST=1.2.3.4 $0  Deploy to different server"
}

# Main execution
main() {
    log_info "Starting Mosh Store deployment to $SERVER_USER@$SERVER_HOST"
    
    case "${1:-}" in
        -h|--help)
            show_usage
            exit 0
            ;;
        -s|--setup-only)
            check_requirements
            test_ssh
            setup_server
            ;;
        -d|--deploy-only)
            check_requirements
            test_ssh
            deploy_app
            verify_deployment
            ;;
        -v|--verify-only)
            test_ssh
            verify_deployment
            ;;
        "")
            check_requirements
            test_ssh
            setup_server
            deploy_app
            verify_deployment
            ;;
        *)
            log_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
    
    log_success "Deployment completed successfully!"
    log_info "Application should be available at:"
    log_info "  - Local: http://$SERVER_HOST:8080/api/v1/"
    log_info "  - Domain: https://shipan.eu/api/v1/ (if configured)"
}

# Run main function
main "$@" 