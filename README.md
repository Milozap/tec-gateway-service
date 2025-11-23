# Gateway Service
This repository contains a Spring Cloud Gateway service for routing requests to downstream microservices discovered via Eureka.
It runs on port 8080 by default.


### Tech stack
- Language: Java 25 (Temurin)
- Frameworks/Libraries: Spring Boot 3.5.x, Spring Cloud 2025.0.x, Netflix Eureka Server
- Build tool: Gradle
- Testing: JUnit Platform via spring-boot-starter-test
- Code coverage: JaCoCo
- Containerization: Docker
- CI: GitHub Actions
- Service Discovery: Eureka Client
- Rate limiting: InMemoryRateLimiter component with configurable window and maxRequests

# Requirements
- [JDK 25](https://www.oracle.com/java/technologies/downloads/#jdk25-linux)
- Docker
- Gradle

# Getting started

1) Clone the repo
```shell
  git clone https://github.com/Milozap/tec-gateway-service
  cd tec-gateway-service
```

2) Build the project
```shell
  ./gradlew clean build
```

3) Run the application (local JVM)
```shell
  ./gradlew bootRun
```

4) Check health endpoint
- http://localhost:8080/actuator/health

### Docker

Build image:
```shell
  docker build -t api-service:local .
```

Run container:
```shell
docker run -p 8082:8082 --name api-service api-service:local
```

### Rate limiting
- Component: com.mzap.gatewayservice.ratelimit.InMemoryRateLimiter
- Behavior: Sliding a time window with a counter per key; window and max requests are configurable

### GitHub Actions (CI)

- The workflow .github/workflows/ci.yml:
    - Builds with Java 25 and Gradle
    - Runs tests and generates JaCoCo coverage
    - Uploads test reports on failure
    - Builds and (on non-PR events) pushes a Docker image to GitHub Container Registry (ghcr.io/milozap/tec-storage-service)
