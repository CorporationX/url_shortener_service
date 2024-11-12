FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY /build/libs/service.jar build/

WORKDIR /app/build
EXPOSE 8088
ENTRYPOINT java -jar service.jar
