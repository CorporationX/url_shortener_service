package faang.school.urlshortenerservice.exception;

public record ErrorResponse(
        int code,
        String message
) {
}
