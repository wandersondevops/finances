# Stage 1: Build the application using Maven
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and compile
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image with a lightweight JDK
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the JAR file from the builder stage (update with actual JAR name)
COPY --from=builder /app/target/Client-Service-0.0.1-SNAPSHOT.jar client-service.jar

# Expose the port for client-service
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "client-service.jar"]
