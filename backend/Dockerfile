# Build stage
FROM maven:3.9.15-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl for health check
RUN apk add --no-cache curl

# Security: Run as non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Health check for container orchestration
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# JVM optimization for production (Render/Heroku-like environments)
# -XX:+UseSerialGC: Single-threaded GC for small containers (< 2 CPU)
# -Xss256k: Reduced stack size to save memory
# -XX:MaxRAMPercentage=70.0: Use up to 70% of container memory
# -XX:+AlwaysActAsServerClassMachine: Always use server JVM optimizations
# NOTE: Removed G1NumReservedPercent (wrong flag name + conflicts with SerialGC)
ENV JAVA_OPTS="-XX:+UseSerialGC -Xss256k -XX:MaxRAMPercentage=70.0 -XX:+AlwaysActAsServerClassMachine"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]