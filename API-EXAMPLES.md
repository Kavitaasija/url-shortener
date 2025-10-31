# URL Shortener - API Examples

Quick reference for testing the URL Shortener API endpoints.

## Prerequisites

First, make sure your service is running and accessible:

```bash
# If running in Kubernetes, port-forward first:
kubectl port-forward svc/url-shortener 8080:8080

# Or if using Docker:
docker-compose up -d
```

---

## API Endpoints

### 1. Health Check

**Check if service is running:**

```bash
curl http://localhost:8080/api
```

**Response:**
```
OK
```

---

### 2. Create Short URL

**Basic Request:**

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": "https://www.google.com"
  }'
```

**With Expiry Time:**

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": "https://github.com/Kavitaasija/url-shortener",
    "expiryInMinutes": 60
  }'
```

**Response Example:**
```json
{
  "shortUrl": "abc123",
  "longUrl": "https://www.google.com",
  "message": "Short URL created successfully"
}
```

**Common Long URLs to Test:**

```bash
# Google
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.google.com"}'

# GitHub
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://github.com"}'

# Long URL with path
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.example.com/very/long/url/path/to/resource?param1=value1&param2=value2"}'
```

---

### 3. Retrieve Long URL

**Get the original URL from short code:**

```bash
# Replace 'abc123' with the actual short URL from the previous response
curl http://localhost:8080/api/urls/abc123
```

**Response Example:**
```json
{
  "shortUrl": "abc123",
  "longUrl": "https://www.google.com"
}
```

---

## Complete Workflow Example

**Step-by-step example:**

```bash
# 1. Create a short URL
RESPONSE=$(curl -s -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.example.com"}')

echo $RESPONSE

# 2. Extract the short URL (if you have jq installed)
SHORT_URL=$(echo $RESPONSE | jq -r '.shortUrl')
echo "Short URL: $SHORT_URL"

# 3. Retrieve the long URL
curl http://localhost:8080/api/urls/$SHORT_URL
```

---

## Error Cases

### Invalid URL Format

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": "not-a-valid-url"
  }'
```

**Response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid URL format"
}
```

### Short URL Not Found

```bash
curl http://localhost:8080/api/urls/nonexistent123
```

**Response:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Short URL not found"
}
```

### Empty Request

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "longUrl is required"
}
```

---

## Testing with Different Tools

### Using httpie (if installed)

```bash
# Create short URL
http POST http://localhost:8080/api/urls longUrl="https://www.google.com"

# Get long URL
http GET http://localhost:8080/api/urls/abc123
```

### Using Postman

1. **Create Short URL:**
   - Method: `POST`
   - URL: `http://localhost:8080/api/urls`
   - Headers: `Content-Type: application/json`
   - Body (raw JSON):
     ```json
     {
       "longUrl": "https://www.google.com",
       "expiryInMinutes": 60
     }
     ```

2. **Get Long URL:**
   - Method: `GET`
   - URL: `http://localhost:8080/api/urls/{shortUrl}`

---

## Automated Test Script

Run all tests at once:

```bash
# Make the script executable
chmod +x test-api.sh

# Run the test script
./test-api.sh
```

---

## Useful One-Liners

**Create and retrieve in one go:**

```bash
SHORT=$(curl -s -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.google.com"}' \
  | jq -r '.shortUrl') && \
curl http://localhost:8080/api/urls/$SHORT | jq
```

**Create multiple URLs:**

```bash
for url in "https://google.com" "https://github.com" "https://stackoverflow.com"; do
  curl -s -X POST http://localhost:8080/api/urls \
    -H "Content-Type: application/json" \
    -d "{\"longUrl\": \"$url\"}" | jq
done
```

**Continuous testing (every 5 seconds):**

```bash
watch -n 5 'curl -s http://localhost:8080/api'
```

---

## Request Format

### CreateShortUrlRequest

```json
{
  "longUrl": "string (required)",
  "expiryInMinutes": "number (optional)"
}
```

**Validation Rules:**
- `longUrl`: Must be a valid URL format
- `longUrl`: Cannot be null or empty
- `expiryInMinutes`: Must be positive if provided

---

## Response Formats

### CreateShortUrlResponse

```json
{
  "shortUrl": "string",
  "longUrl": "string",
  "message": "string"
}
```

### GetLongUrlResponse

```json
{
  "shortUrl": "string",
  "longUrl": "string"
}
```

### ErrorResponse

```json
{
  "status": "number",
  "error": "string",
  "message": "string"
}
```

---

## Tips

1. **Pretty Print JSON:** Add `| jq` to any curl command for formatted output
2. **Save Response:** Add `-o response.json` to save the response to a file
3. **Verbose Mode:** Add `-v` to see detailed request/response headers
4. **Silent Mode:** Add `-s` to hide progress output
5. **Follow Redirects:** Add `-L` if the service redirects

---

## Troubleshooting

**Connection Refused:**
```bash
# Check if service is running
kubectl get pods
kubectl logs -f -l app.kubernetes.io/name=url-shortener

# Check if port-forward is active
kubectl port-forward svc/url-shortener 8080:8080
```

**Timeout:**
```bash
# Increase timeout
curl --max-time 30 http://localhost:8080/api/urls
```

**SSL/TLS Errors:**
```bash
# Skip certificate verification (local testing only)
curl -k https://localhost:8080/api/urls
```

---

## Quick Reference Card

| Action | Command |
|--------|---------|
| Health Check | `curl http://localhost:8080/api` |
| Create Short URL | `curl -X POST http://localhost:8080/api/urls -H "Content-Type: application/json" -d '{"longUrl": "https://example.com"}'` |
| Get Long URL | `curl http://localhost:8080/api/urls/{shortUrl}` |
| Port Forward | `kubectl port-forward svc/url-shortener 8080:8080` |
| View Logs | `kubectl logs -f -l app.kubernetes.io/name=url-shortener` |

---

## Next Steps

1. Try creating a short URL with the examples above
2. Save the short URL from the response
3. Use that short URL to retrieve the long URL
4. Run the automated test script: `./test-api.sh`

For more details, see [DEPLOYMENT.md](./DEPLOYMENT.md)

