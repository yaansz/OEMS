# Multi-stage build for MessageSender service
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose application port (if needed for actuator)
EXPOSE 8081

# Health check (adjust if MessageSender doesn't have actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD ps aux | grep java || exit 1

# Run the application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
