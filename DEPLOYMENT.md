# URL Shortener - Deployment Guide

This guide provides comprehensive instructions for deploying the URL Shortener application using Docker and Kubernetes with Helm.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development with Docker](#local-development-with-docker)
3. [Kubernetes Deployment with Helm](#kubernetes-deployment-with-helm)
4. [Configuration](#configuration)
5. [Monitoring and Troubleshooting](#monitoring-and-troubleshooting)
6. [Production Best Practices](#production-best-practices)

---

## Prerequisites

### Required Tools

- **Java 17+** - For local development
- **Gradle 8.x** - Build tool
- **Docker 20.x+** - Container runtime
- **Docker Compose 2.x+** - Local container orchestration
- **Kubernetes 1.19+** - Container orchestration platform
- **Helm 3.x+** - Kubernetes package manager
- **kubectl** - Kubernetes CLI

### Verify Installation

```bash
java -version
gradle --version
docker --version
docker-compose --version
kubectl version --client
helm version
```

---

## Local Development with Docker

### Quick Start with Docker Compose

The fastest way to run the application locally:

```bash
# Build and run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

### Manual Docker Build and Run

```bash
# Build the Docker image
docker build -t url-shortener:latest .

# Run the container
docker run -d \
  --name url-shortener \
  -p 8080:8080 \
  -e SERVER_PORT=8080 \
  url-shortener:latest

# View logs
docker logs -f url-shortener

# Stop and remove container
docker stop url-shortener
docker rm url-shortener
```

### Using Makefile

We provide a Makefile for convenience:

```bash
# View all available commands
make help

# Build the application
make build

# Build Docker image
make docker-build

# Run with Docker Compose
make docker-run

# View logs
make docker-logs

# Stop containers
make docker-stop

# Clean up
make docker-clean
```

### Test the API

Once running, test the application:

```bash
# Create a short URL
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": "https://www.example.com/very/long/path",
    "expiryInMinutes": 60
  }'

# Response example:
# {
#   "shortUrl": "abc123",
#   "longUrl": "https://www.example.com/very/long/path",
#   "createdAt": "2025-10-31T10:00:00",
#   "expiresAt": "2025-10-31T11:00:00"
# }

# Retrieve the long URL
curl http://localhost:8080/api/urls/abc123
```

---

## Kubernetes Deployment with Helm

### Step 1: Build and Push Docker Image

First, build your Docker image and push it to a container registry:

```bash
# Build the image
docker build -t url-shortener:1.0.0 .

# Tag for your registry (replace with your registry)
docker tag url-shortener:1.0.0 your-registry.io/url-shortener:1.0.0

# Push to registry
docker push your-registry.io/url-shortener:1.0.0
```

For local Kubernetes (like Minikube or Kind), you can use the local image:

```bash
# For Minikube
eval $(minikube docker-env)
docker build -t url-shortener:1.0.0 .

# For Kind
kind load docker-image url-shortener:1.0.0
```

### Step 2: Install with Helm

#### Basic Installation

```bash
# Install with default values
helm install url-shortener ./helm/url-shortener

# Or using Makefile
make helm-install
```

#### Custom Installation

```bash
# Install with custom image
helm install url-shortener ./helm/url-shortener \
  --set image.repository=your-registry.io/url-shortener \
  --set image.tag=1.0.0 \
  --set replicaCount=3

# Install with custom namespace
helm install url-shortener ./helm/url-shortener \
  --namespace url-shortener \
  --create-namespace
```

#### Using Custom Values File

Create a custom values file (e.g., `my-values.yaml`):

```yaml
replicaCount: 3

image:
  repository: your-registry.io/url-shortener
  tag: "1.0.0"
  pullPolicy: IfNotPresent

resources:
  limits:
    cpu: 1000m
    memory: 512Mi
  requests:
    cpu: 500m
    memory: 256Mi

ingress:
  enabled: true
  className: nginx
  hosts:
    - host: url-shortener.example.com
      paths:
        - path: /
          pathType: Prefix
```

Install with custom values:

```bash
helm install url-shortener ./helm/url-shortener -f my-values.yaml
```

### Step 3: Verify Deployment

```bash
# Check Helm release
helm status url-shortener

# Check pods
kubectl get pods -l app.kubernetes.io/name=url-shortener

# Check service
kubectl get svc url-shortener

# View logs
kubectl logs -f deployment/url-shortener
```

### Step 4: Access the Application

#### Port Forward (for testing)

```bash
# Port forward to local machine
kubectl port-forward svc/url-shortener 8080:8080

# Or using Makefile
make k8s-port-forward

# Access the application
curl http://localhost:8080/api/urls
```

#### Using Ingress (for production)

Enable ingress in values:

```yaml
ingress:
  enabled: true
  className: nginx
  hosts:
    - host: url-shortener.example.com
      paths:
        - path: /
          pathType: Prefix
```

### Step 5: Upgrade Deployment

```bash
# Upgrade with new image tag
helm upgrade url-shortener ./helm/url-shortener \
  --set image.tag=1.1.0

# Or using Makefile
make helm-upgrade IMAGE_TAG=1.1.0

# Upgrade with new values file
helm upgrade url-shortener ./helm/url-shortener -f my-values.yaml
```

### Step 6: Uninstall

```bash
# Uninstall the release
helm uninstall url-shortener

# Or using Makefile
make helm-uninstall
```

---

## Configuration

### Environment Variables

The application can be configured using environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | `8080` |
| `SPRING_APPLICATION_NAME` | Application name | `url-shortener` |
| `LOGGING_LEVEL_ORG_URL_SHORTENER` | Log level | `INFO` |

### Helm Values

Key configuration options in `values.yaml`:

```yaml
# Replica count
replicaCount: 2

# Image configuration
image:
  repository: url-shortener
  tag: latest
  pullPolicy: IfNotPresent

# Resource limits
resources:
  limits:
    cpu: 1000m
    memory: 512Mi
  requests:
    cpu: 500m
    memory: 256Mi

# Autoscaling
autoscaling:
  enabled: false
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80

# Ingress
ingress:
  enabled: false
  className: nginx
  hosts:
    - host: url-shortener.local
      paths:
        - path: /
          pathType: Prefix
```

---

## Monitoring and Troubleshooting

### View Logs

```bash
# Docker Compose
docker-compose logs -f

# Kubernetes
kubectl logs -f -l app.kubernetes.io/name=url-shortener

# Or using Makefile
make k8s-logs
```

### Check Pod Status

```bash
# Get pods
kubectl get pods -l app.kubernetes.io/name=url-shortener

# Describe pod
kubectl describe pod <pod-name>

# Or using Makefile
make k8s-pods
```

### Health Checks

The Docker image includes health checks:

```bash
# Check container health
docker inspect --format='{{.State.Health.Status}}' url-shortener
```

### Common Issues

#### Pod Not Starting

```bash
# Check events
kubectl describe pod <pod-name>

# Check logs
kubectl logs <pod-name>

# Common causes:
# - Image pull errors
# - Resource constraints
# - Configuration errors
```

#### Service Not Accessible

```bash
# Check service
kubectl get svc url-shortener
kubectl describe svc url-shortener

# Check endpoints
kubectl get endpoints url-shortener
```

#### Image Pull Errors

```bash
# For local development, ensure image is loaded:
# Minikube
eval $(minikube docker-env)
docker build -t url-shortener:1.0.0 .

# Kind
kind load docker-image url-shortener:1.0.0
```

---

## Production Best Practices

### 1. Use Specific Image Tags

Never use `latest` in production:

```yaml
image:
  repository: your-registry.io/url-shortener
  tag: "1.0.0"  # Use specific version
```

### 2. Enable Resource Limits

Always set resource limits:

```yaml
resources:
  limits:
    cpu: 2000m
    memory: 1Gi
  requests:
    cpu: 1000m
    memory: 512Mi
```

### 3. Enable Autoscaling

```yaml
autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 20
  targetCPUUtilizationPercentage: 70
```

### 4. Enable Pod Disruption Budget

```yaml
podDisruptionBudget:
  enabled: true
  minAvailable: 2
```

### 5. Use Ingress with TLS

```yaml
ingress:
  enabled: true
  className: nginx
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
  hosts:
    - host: url-shortener.example.com
      paths:
        - path: /
          pathType: Prefix
  tls:
    - secretName: url-shortener-tls
      hosts:
        - url-shortener.example.com
```

### 6. Multi-Environment Setup

Create separate values files for each environment:

```bash
# Development
helm install url-shortener ./helm/url-shortener -f values-dev.yaml

# Staging
helm install url-shortener ./helm/url-shortener -f values-staging.yaml

# Production
helm install url-shortener ./helm/url-shortener -f values-prod.yaml
```

### 7. CI/CD Integration

Example GitHub Actions workflow:

```yaml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker image
        run: docker build -t url-shortener:${{ github.sha }} .
      
      - name: Push to registry
        run: |
          docker tag url-shortener:${{ github.sha }} your-registry.io/url-shortener:${{ github.sha }}
          docker push your-registry.io/url-shortener:${{ github.sha }}
      
      - name: Deploy with Helm
        run: |
          helm upgrade --install url-shortener ./helm/url-shortener \
            --set image.tag=${{ github.sha }}
```

---

## Quick Reference

### Docker Commands

```bash
make build                # Build application
make docker-build         # Build Docker image
make docker-run          # Run with Docker Compose
make docker-stop         # Stop containers
make docker-clean        # Clean up
```

### Helm Commands

```bash
make helm-lint           # Lint Helm chart
make helm-install        # Install chart
make helm-upgrade        # Upgrade release
make helm-uninstall      # Uninstall release
make helm-status         # Check status
```

### Kubernetes Commands

```bash
make k8s-pods           # List pods
make k8s-logs           # View logs
make k8s-port-forward   # Port forward
```

---

## Support

For issues and questions:
- Check logs: `make k8s-logs` or `make docker-logs`
- Review pod status: `make k8s-pods`
- Check Helm status: `make helm-status`

For more details, refer to:
- [Helm Documentation](./helm/README.md)
- [Application README](./README.md)

