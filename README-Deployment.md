# Mosh Store - Deployment Guide

This guide provides complete instructions for deploying the Mosh Store Spring Boot application to your Ubuntu server using Docker and GitHub Actions.

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Ubuntu Server (shipan.eu)                │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Cloudflare    │  │      Nginx      │  │    Docker    │ │
│  │   (SSL/CDN)     │──│ (Reverse Proxy) │──│  Containers  │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
│                                             │              │ │
│                                             │ ┌──────────┐ │ │
│                                             │ │   App    │ │ │
│                                             │ │(Port 8080)│ │ │
│                                             │ └──────────┘ │ │
│                                             │ ┌──────────┐ │ │
│                                             │ │  MySQL   │ │ │
│                                             │ │(Port 3306)│ │ │
│                                             │ └──────────┘ │ │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Option 1: Automated Deployment with GitHub Actions (Recommended)

1. **Fork/Clone this repository**
2. **Set up GitHub Secrets** (see [GitHub Actions Setup](#github-actions-setup))
3. **Push to main branch** - deployment will trigger automatically

### Option 2: Manual Deployment

1. **Run the deployment script:**
```bash
./deploy.sh
```

## 📋 Prerequisites

### Local Requirements
- Git
- SSH access to your Ubuntu server
- SSH key configured (`~/.ssh/id_ed25519`)

### Server Requirements
- Ubuntu 20.04+ server
- SSH access
- Domain name pointing to server (shipan.eu)
- Cloudflare account (optional, for SSL)

## 🔧 Manual Setup Instructions

### 1. Server Preparation

Connect to your server:
```bash
ssh shipi-outside
# or
ssh shipan@100.82.121.103
```

Run the server setup:
```bash
# Download and run the server setup script
curl -fsSL https://raw.githubusercontent.com/your-username/Mosh-Store-SpringBoot/main/server-setup.sh | bash

# Or manually follow server-setup.md
```

### 2. Application Deployment

From your local machine:

```bash
# Clone the repository
git clone https://github.com/your-username/Mosh-Store-SpringBoot.git
cd Mosh-Store-SpringBoot

# Make deployment script executable
chmod +x deploy.sh

# Deploy to server
./deploy.sh
```

### 3. Environment Configuration

On your server, edit the environment file:
```bash
cd /opt/apps/mosh-store
nano .env
```

Update these critical values:
```env
# Database
DB_PASSWORD=your_secure_mysql_password
DB_USERNAME=moshuser

# Application
SPRING_PROFILES_ACTIVE=prod

# Security (generate strong keys!)
JWT_SECRET=your_jwt_secret_key_here
```

### 4. Start the Application

```bash
cd /opt/apps/mosh-store
docker-compose up -d
```

## 🤖 GitHub Actions Setup

### Required Secrets

Add these secrets in your GitHub repository settings (`Settings > Secrets and variables > Actions`):

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `SSH_PRIVATE_KEY` | Your SSH private key | Contents of `~/.ssh/id_ed25519` |
| `SERVER_HOST` | Server IP or hostname | `100.82.121.103` |
| `SERVER_USER` | SSH username | `shipan` |
| `DB_USERNAME` | Database username | `moshuser` |
| `DB_PASSWORD` | Database password | `secure_password123` |

### Setting Up Secrets

1. **SSH Private Key:**
```bash
# Copy your private key
cat ~/.ssh/id_ed25519
# Copy the entire output and paste as SSH_PRIVATE_KEY secret
```

2. **Server Details:**
- `SERVER_HOST`: `100.82.121.103` (your Tailscale IP)
- `SERVER_USER`: `shipan`

3. **Database Credentials:**
- Generate a secure password for `DB_PASSWORD`
- Use `moshuser` for `DB_USERNAME`

### GitHub Actions Workflow

The workflow automatically:
1. ✅ Runs tests on every push
2. ✅ Builds Docker image on main branch
3. ✅ Pushes image to GitHub Container Registry
4. ✅ Deploys to your server
5. ✅ Verifies deployment health

## 🌐 Domain and SSL Setup

### Cloudflare Configuration

1. **Add your domain to Cloudflare**
2. **Update DNS records:**
   ```
   Type: A
   Name: @
   Content: 100.82.121.103
   Proxy: Enabled (orange cloud)
   
   Type: CNAME
   Name: mosh-store
   Content: shipan.eu
   Proxy: Enabled
   ```

3. **SSL/TLS Settings:**
   - Go to SSL/TLS > Overview
   - Set encryption mode to "Full (strict)"
   - Go to SSL/TLS > Origin Server
   - Create Origin Certificate
   - Save certificate files on server

### SSL Certificate Installation

On your server:
```bash
# Create SSL directory
sudo mkdir -p /opt/apps/shared/ssl

# Save Cloudflare Origin Certificate
sudo nano /opt/apps/shared/ssl/cloudflare-origin.pem
# Paste certificate content

# Save private key
sudo nano /opt/apps/shared/ssl/cloudflare-origin.key
# Paste private key content

# Set permissions
sudo chmod 600 /opt/apps/shared/ssl/*
sudo chown root:root /opt/apps/shared/ssl/*
```

## 🔍 Verification and Testing

### Health Checks

1. **Application Health:**
```bash
curl http://localhost:8080/api/v1/
curl https://shipan.eu/api/v1/
```

2. **Container Status:**
```bash
cd /opt/apps/mosh-store
docker-compose ps
docker-compose logs app
```

3. **Database Connection:**
```bash
docker exec -it mosh-store-mysql mysql -u moshuser -p
```

### API Testing

Test your API endpoints:
```bash
# Health check
curl https://shipan.eu/api/v1/

# API documentation
curl https://shipan.eu/swagger-ui.html

# Test specific endpoints
curl https://shipan.eu/api/v1/users
```

## 📊 Monitoring and Maintenance

### Log Management

```bash
# View application logs
docker-compose logs -f app

# View all container logs
docker-compose logs

# View system logs
sudo journalctl -u docker
```

### Performance Monitoring

```bash
# Container resource usage
docker stats

# System resources
htop
df -h

# Network connections
sudo netstat -tulpn
```

### Database Management

```bash
# Connect to MySQL
docker exec -it mosh-store-mysql mysql -u root -p

# Backup database
docker exec mosh-store-mysql mysqldump -u root -p$DB_PASSWORD cdb_mosh_p1_ecommerce_app_prod > backup.sql

# Restore database
docker exec -i mosh-store-mysql mysql -u root -p$DB_PASSWORD cdb_mosh_p1_ecommerce_app_prod < backup.sql
```

## 🔧 Troubleshooting

### Common Issues

1. **Port Already in Use:**
```bash
sudo netstat -tulpn | grep :8080
# Kill process using the port or change APP_PORT in .env
```

2. **Database Connection Failed:**
```bash
# Check MySQL container
docker-compose logs mysql
# Verify credentials in .env file
```

3. **SSL Certificate Issues:**
```bash
# Check certificate files
sudo ls -la /opt/apps/shared/ssl/
# Verify Nginx configuration
docker-compose logs nginx
```

4. **Memory Issues:**
```bash
# Check available memory
free -h
# Adjust JVM memory settings in Dockerfile
```

### Debug Commands

```bash
# Check Docker networks
docker network ls
docker network inspect mosh-store_mosh-store-network

# Check container details
docker inspect mosh-store-app

# Test database connection
docker exec mosh-store-mysql mysqladmin -u root -p$DB_PASSWORD ping

# Check application properties
docker exec mosh-store-app env | grep -E "(DB_|SPRING_)"
```

## 🔄 Updates and Deployment

### Manual Updates

```bash
# Pull latest code
git pull origin main

# Rebuild and restart
docker-compose down
docker-compose up -d --build

# Or use the deployment script
./deploy.sh --deploy-only
```

### Automated Updates

Push to main branch and GitHub Actions will automatically:
1. Run tests
2. Build new image
3. Deploy to server
4. Verify health

## 📈 Scaling and Future Enhancements

### Adding More Applications

Follow the server organization structure:
```bash
/opt/apps/
├── mosh-store/          # Current app
├── new-app-1/          # Future app
└── new-app-2/          # Another app
```

### Load Balancing

For high traffic, consider:
- Multiple application instances
- Database read replicas
- Redis caching layer
- CDN for static assets

### Monitoring Stack

Add comprehensive monitoring:
- Prometheus + Grafana
- ELK Stack (Elasticsearch, Logstash, Kibana)
- Application Performance Monitoring (APM)

## 🆘 Support

### Useful Links
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Nginx Documentation](https://nginx.org/en/docs/)
- [Cloudflare Documentation](https://developers.cloudflare.com/)

### Getting Help

1. Check application logs: `docker-compose logs app`
2. Review this documentation
3. Check GitHub Issues
4. Test locally first with `docker-compose up`

---

## 🎉 Success!

Your Mosh Store application should now be running at:
- **Local**: http://100.82.121.103:8080/api/v1/
- **Public**: https://shipan.eu/api/v1/
- **API Docs**: https://shipan.eu/swagger-ui.html

Congratulations on successfully deploying your Spring Boot application! 🚀 