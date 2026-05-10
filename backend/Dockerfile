# Bước 1: Build ứng dụng
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Bước 2: Chạy ứng dụng
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Cổng mặc định của Render là 8080 (khớp với code của bạn)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
