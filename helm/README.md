# URL Shortener Helm Chart

This Helm chart deploys the URL Shortener application on a Kubernetes cluster.

## Prerequisites

- Kubernetes 1.19+
- Helm 3.0+
- Docker (for building the image)

## Building the Docker Image

Before deploying with Helm, build the Docker image:

```bash
# From the project root directory
docker build -t url-shortener:latest .

# Optional: Tag and push to your registry
docker tag url-shortener:latest your-registry/url-shortener:1.0.0
docker push your-registry/url-shortener:1.0.0
```

## Installing the Chart

### Install with default values

```bash
# From the helm directory
helm install url-shortener ./url-shortener

# Or from the project root
helm install url-shortener ./helm/url-shortener
```

### Install with custom values

```bash
helm install url-shortener ./url-shortener \
  --set image.repository=your-registry/url-shortener \
  --set image.tag=1.0.0 \
  --set replicaCount=3
```

### Install with custom values file

```bash
helm install url-shortener ./url-shortener -f custom-values.yaml
```

## Uninstalling the Chart

```bash
helm uninstall url-shortener
```

## Configuration

The following table lists the configurable parameters of the URL Shortener chart and their default values.

| Parameter | Description | Default |
|-----------|-------------|---------|
| `replicaCount` | Number of replicas | `2` |
| `image.repository` | Image repository | `url-shortener` |
| `image.tag` | Image tag | `latest` |
| `image.pullPolicy` | Image pull policy | `IfNotPresent` |
| `service.type` | Kubernetes service type | `ClusterIP` |
| `service.port` | Service port | `8080` |
| `ingress.enabled` | Enable ingress | `false` |
| `ingress.className` | Ingress class name | `nginx` |
| `ingress.hosts` | Ingress hosts | `[{host: url-shortener.local, paths: [{path: /, pathType: Prefix}]}]` |
| `resources.limits.cpu` | CPU limit | `1000m` |
| `resources.limits.memory` | Memory limit | `512Mi` |
| `resources.requests.cpu` | CPU request | `500m` |
| `resources.requests.memory` | Memory request | `256Mi` |
| `autoscaling.enabled` | Enable HPA | `false` |
| `autoscaling.minReplicas` | Minimum replicas | `2` |
| `autoscaling.maxReplicas` | Maximum replicas | `10` |
| `autoscaling.targetCPUUtilizationPercentage` | Target CPU utilization | `80` |

## Examples

### Example 1: Enable Ingress

```bash
helm install url-shortener ./url-shortener \
  --set ingress.enabled=true \
  --set ingress.hosts[0].host=url-shortener.example.com \
  --set ingress.hosts[0].paths[0].path=/ \
  --set ingress.hosts[0].paths[0].pathType=Prefix
```

### Example 2: Enable Autoscaling

```bash
helm install url-shortener ./url-shortener \
  --set autoscaling.enabled=true \
  --set autoscaling.minReplicas=2 \
  --set autoscaling.maxReplicas=10 \
  --set autoscaling.targetCPUUtilizationPercentage=80
```

### Example 3: Production Configuration

Create a `production-values.yaml` file:

```yaml
replicaCount: 3

image:
  repository: your-registry/url-shortener
  tag: "1.0.0"
  pullPolicy: IfNotPresent

resources:
  limits:
    cpu: 2000m
    memory: 1Gi
  requests:
    cpu: 1000m
    memory: 512Mi

autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 20
  targetCPUUtilizationPercentage: 70
  targetMemoryUtilizationPercentage: 80

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

podDisruptionBudget:
  enabled: true
  minAvailable: 2
```

Then install:

```bash
helm install url-shortener ./url-shortener -f production-values.yaml
```

## Upgrading

```bash
helm upgrade url-shortener ./url-shortener --set image.tag=2.0.0
```

## Testing

Test the deployment:

```bash
# Port forward to access the service
kubectl port-forward svc/url-shortener 8080:8080

# Test the API
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com","expiryInMinutes":60}'
```

## Monitoring

### View logs

```bash
# Get pod name
kubectl get pods -l app.kubernetes.io/name=url-shortener

# View logs
kubectl logs -f <pod-name>
```

### Check deployment status

```bash
helm status url-shortener
helm list
kubectl get all -l app.kubernetes.io/name=url-shortener
```

## Troubleshooting

### Pod is not starting

```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

### Service is not accessible

```bash
kubectl get svc url-shortener
kubectl describe svc url-shortener
```

### Check Helm release

```bash
helm get values url-shortener
helm get manifest url-shortener
```

