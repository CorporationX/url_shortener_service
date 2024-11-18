FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY service.jar /app/url_shortener_service.jar
ENTRYPOINT ["java", "-jar", "url_shortener_service.jar"]
