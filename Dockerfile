FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY . .

RUN gradle clean build -x test --scan


FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]