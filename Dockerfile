# Stage 1: Build ứng dụng
FROM maven:3.6.3-openjdk-8 AS builder

WORKDIR /app

# Copy file pom.xml và download dependencies trước
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ source code
COPY src ./src

# Build project (bỏ qua test cho nhanh nếu cần)
RUN mvn clean package -DskipTests

# Stage 2: Run ứng dụng
FROM openjdk:8-jdk-alpine

WORKDIR /app

# Copy file jar từ builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port của Spring Boot (8080 mặc định)
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
