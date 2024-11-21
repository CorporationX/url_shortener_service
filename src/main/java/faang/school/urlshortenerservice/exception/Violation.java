package faang.school.urlshortenerservice.exception;

public record Violation(
        String fieldName,
        String message) {
}
