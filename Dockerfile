FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY build/libs/service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]