package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.enums.ErrorReason;

public record ErrorResponseDto(
        String status,
        String reason,
        String message,
        String timestamp) {

    public ErrorResponseDto(String status, ErrorReason reason, String message, String timestamp) {
        this(status, reason.getMessage(), message, timestamp);
    }
}
