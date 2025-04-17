# Собираем JAR в отдельном образе
FROM gradle:7.4-jdk17 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon

# Финальный образ
FROM --platform=linux/amd64 eclipse-temurin:17-jdk
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
