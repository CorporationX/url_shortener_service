package faang.school.urlshortenerservice.dto;

public record ErrorResponseDto(
        String status,
        String message,
        String timestamp
) {
}
