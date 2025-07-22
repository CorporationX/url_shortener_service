package faang.school.urlshortenerservice.dto;

public record ErrorResponseDto(
        String status,
        String reason,
        String message,
        String timestamp) {
}
