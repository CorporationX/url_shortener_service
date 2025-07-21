FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY src/main/resources/application-docker.yaml ./application-docker.yaml
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
