package faang.school.urlshortenerservice.dto;

public record ErrorModel(String message, int statusCode, String serviceName) {
}