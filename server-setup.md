# Ubuntu Server Setup Guide for Multiple Applications

This guide helps you organize your Ubuntu server to run multiple applications in isolated environments using Docker and proper directory structure.

## 🏗️ Server Architecture

```
/opt/
├── apps/                           # Main applications directory
│   ├── mosh-store/                # Your current Spring Boot app
│   │   ├── docker-compose.yml
│   │   ├── .env
│   │   ├── docker/
│   │   └── logs/
│   ├── future-app-1/              # Future applications
│   ├── future-app-2/
│   └── shared/                    # Shared resources
│       ├── nginx/                 # Global nginx config
│       ├── ssl/                   # SSL certificates
│       └── monitoring/            # Monitoring stack
├── data/                          # Persistent data
│   ├── mysql/                     # Database data
│   ├── redis/                     # Cache data
│   └── backups/                   # Backup storage
└── scripts/                       # Management scripts
    ├── backup.sh
    ├── deploy.sh
    └── monitoring.sh
```

## 🚀 Initial Server Setup

### 1. Connect to Your Server
```bash
# Using your existing SSH config
ssh shipi-outside

# Or directly
ssh shipan@100.82.121.103
```

### 2. Update System
```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl wget git htop tree ufw fail2ban
```

### 3. Setup Firewall
```bash
# Enable UFW
sudo ufw enable

# Allow SSH (your current connection)
sudo ufw allow ssh

# Allow HTTP and HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Allow specific ports for development (optional)
sudo ufw allow 8080/tcp  # Spring Boot
sudo ufw allow 3306/tcp  # MySQL (only if needed externally)

# Check status
sudo ufw status
```

### 4. Install Docker and Docker Compose
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose V2
sudo apt install -y docker-compose-plugin

# Logout and login again to apply group changes
exit
ssh shipi-outside

# Verify installation
docker --version
docker compose version
```

### 5. Create Directory Structure
```bash
# Create main directories
sudo mkdir -p /opt/{apps,data,scripts}
sudo mkdir -p /opt/apps/{mosh-store,shared}
sudo mkdir -p /opt/apps/shared/{nginx,ssl,monitoring}
sudo mkdir -p /opt/data/{mysql,redis,backups}

# Set ownership
sudo chown -R $USER:$USER /opt/apps
sudo chown -R $USER:$USER /opt/scripts
sudo chown -R root:root /opt/data  # Keep data secure

# Set permissions
chmod 755 /opt/apps
chmod 755 /opt/scripts
chmod 750 /opt/data
```

## 🐳 Docker Network Setup

Create a shared network for all applications:

```bash
# Create a shared Docker network
docker network create --driver bridge shared-network
```

## 📊 Monitoring Setup (Optional)

### Install Portainer for Docker Management
```bash
# Create portainer volume
docker volume create portainer_data

# Run Portainer
docker run -d \
  --name portainer \
  --restart unless-stopped \
  -p 9443:9443 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v portainer_data:/data \
  portainer/portainer-ce:latest

# Access at: https://your-server:9443
```

### Setup System Monitoring (Optional)
```bash
# Install htop, iotop, and other monitoring tools
sudo apt install -y htop iotop nethogs ncdu

# Setup log rotation
sudo tee /etc/logrotate.d/docker-logs << 'EOF'
/opt/apps/*/logs/*.log {
    daily
    missingok
    rotate 14
    compress
    notifempty
    create 644 root root
}
EOF
```

## 🔒 Security Hardening

### 1. Setup Fail2Ban
```bash
# Configure fail2ban for SSH
sudo tee /etc/fail2ban/jail.local << 'EOF'
[DEFAULT]
bantime = 3600
findtime = 600
maxretry = 3

[sshd]
enabled = true
port = ssh
logpath = /var/log/auth.log
maxretry = 3
EOF

sudo systemctl restart fail2ban
```

### 2. Configure Automatic Updates
```bash
sudo apt install -y unattended-upgrades
sudo dpkg-reconfigure -plow unattended-upgrades
```

### 3. Setup SSL Certificates (for Cloudflare)
```bash
# Create SSL directory
sudo mkdir -p /opt/apps/shared/ssl

# If using Cloudflare Origin Certificate:
# 1. Go to Cloudflare Dashboard > SSL/TLS > Origin Server
# 2. Create Origin Certificate
# 3. Save the certificate and key files:
sudo tee /opt/apps/shared/ssl/cloudflare-origin.pem << 'EOF'
# Paste your Cloudflare Origin Certificate here
EOF

sudo tee /opt/apps/shared/ssl/cloudflare-origin.key << 'EOF'
# Paste your Cloudflare Origin Private Key here
EOF

# Set proper permissions
sudo chmod 600 /opt/apps/shared/ssl/*
sudo chown root:root /opt/apps/shared/ssl/*
```

## 🌐 Global Nginx Setup (Reverse Proxy)

### Create Global Nginx Configuration
```bash
# Create global nginx directory
mkdir -p /opt/apps/shared/nginx/{conf.d,sites-available,sites-enabled}

# Create main nginx.conf
tee /opt/apps/shared/nginx/nginx.conf << 'EOF'
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log notice;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
    use epoll;
    multi_accept on;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Logging
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';
    access_log /var/log/nginx/access.log main;

    # Basic Settings
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;
    client_max_body_size 50M;

    # Gzip
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Rate Limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=1r/s;

    # Include site configurations
    include /etc/nginx/conf.d/*.conf;
    include /etc/nginx/sites-enabled/*;
}
EOF

# Create site configuration for mosh-store
tee /opt/apps/shared/nginx/sites-available/mosh-store << 'EOF'
# Mosh Store Configuration
upstream mosh_store {
    server localhost:8080 max_fails=3 fail_timeout=30s;
}

server {
    listen 80;
    server_name shipan.eu mosh-store.shipan.eu;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name shipan.eu mosh-store.shipan.eu;

    # SSL Configuration
    ssl_certificate /etc/ssl/certs/cloudflare-origin.pem;
    ssl_certificate_key /etc/ssl/private/cloudflare-origin.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # Security Headers
    add_header Strict-Transport-Security "max-age=63072000" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;

    # Cloudflare Real IP
    set_real_ip_from 173.245.48.0/20;
    set_real_ip_from 103.21.244.0/22;
    # ... (add other Cloudflare IP ranges)
    real_ip_header CF-Connecting-IP;

    # API Routes
    location /api/ {
        limit_req zone=api burst=20 nodelay;
        proxy_pass http://mosh_store;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Health Check
    location /health {
        access_log off;
        proxy_pass http://mosh_store/api/v1/;
    }

    # Default route
    location / {
        return 301 /api/v1/;
    }
}
EOF

# Enable the site
ln -sf /opt/apps/shared/nginx/sites-available/mosh-store /opt/apps/shared/nginx/sites-enabled/
```

## 📁 Application Deployment Structure

Each application should follow this structure:

```
/opt/apps/your-app/
├── docker-compose.yml          # App-specific compose file
├── .env                        # Environment variables
├── .env.example               # Example environment file
├── docker/                    # Docker configurations
│   ├── app/                   # Application specific configs
│   └── nginx/                 # App-specific nginx configs (if needed)
├── data/                      # App-specific data (if any)
├── logs/                      # Application logs
└── scripts/                   # App-specific scripts
    ├── deploy.sh
    ├── backup.sh
    └── maintenance.sh
```

## 🔄 Backup Strategy

### Create Backup Script
```bash
tee /opt/scripts/backup.sh << 'EOF'
#!/bin/bash
# Backup script for all applications

BACKUP_DIR="/opt/data/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p "$BACKUP_DIR/$DATE"

# Backup databases
docker exec mosh-store-mysql mysqldump -u root -p$DB_PASSWORD --all-databases > "$BACKUP_DIR/$DATE/mysql_all_databases.sql"

# Backup application data
tar -czf "$BACKUP_DIR/$DATE/app_data.tar.gz" /opt/apps/

# Keep only last 7 days of backups
find "$BACKUP_DIR" -type d -mtime +7 -exec rm -rf {} +

echo "Backup completed: $BACKUP_DIR/$DATE"
EOF

chmod +x /opt/scripts/backup.sh

# Setup cron job for daily backups
(crontab -l 2>/dev/null; echo "0 2 * * * /opt/scripts/backup.sh >> /var/log/backup.log 2>&1") | crontab -
```

## 🚀 Deploy Your Application

1. **Copy your deployment files to the server:**
```bash
# From your local machine
scp -r docker-compose.yml docker/ deploy.sh shipi-outside:/opt/apps/mosh-store/
```

2. **Setup environment:**
```bash
# On the server
cd /opt/apps/mosh-store
cp .env.example .env
# Edit .env with your actual values
nano .env
```

3. **Deploy:**
```bash
# Run your deployment script
./deploy.sh
```

## 📊 Monitoring and Maintenance

### Check Application Status
```bash
# Check all containers
docker ps -a

# Check specific app
cd /opt/apps/mosh-store && docker-compose ps

# Check logs
docker-compose logs -f app

# Check system resources
htop
df -h
docker system df
```

### Maintenance Commands
```bash
# Update application
cd /opt/apps/mosh-store
git pull  # if using git
docker-compose pull
docker-compose up -d

# Clean up Docker
docker system prune -f
docker image prune -f

# Check disk usage
ncdu /opt/
```

## 🔧 Troubleshooting

### Common Issues

1. **Port conflicts:** Use different ports for each application
2. **Permission issues:** Check file ownership and permissions
3. **Network issues:** Ensure Docker networks are properly configured
4. **SSL issues:** Verify certificate paths and permissions

### Useful Commands
```bash
# Check port usage
sudo netstat -tulpn | grep :80
sudo netstat -tulpn | grep :443

# Check Docker networks
docker network ls
docker network inspect shared-network

# Check system logs
sudo journalctl -u docker
sudo tail -f /var/log/nginx/error.log
```

## 🎯 Next Steps

1. **Deploy your Mosh Store application** using the provided scripts
2. **Setup monitoring** with Portainer or other tools
3. **Configure automated backups**
4. **Setup log aggregation** (ELK stack or similar)
5. **Add more applications** following the same structure
6. **Setup CI/CD pipelines** for automated deployments

This structure allows you to:
- ✅ Run multiple applications in isolation
- ✅ Share common resources (databases, reverse proxy)
- ✅ Maintain security and organization
- ✅ Scale easily as you add more applications
- ✅ Monitor and maintain everything centrally 