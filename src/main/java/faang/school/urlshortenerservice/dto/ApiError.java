package faang.school.urlshortenerservice.dto;

public record ApiError(String message, int status, String method, String path, String timestamp) {
}
