package faang.school.urlshortenerservice.dto;

public record ApiError(String message, int status, String path, String timestamp) {
}
