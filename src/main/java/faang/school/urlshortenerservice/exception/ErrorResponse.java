package faang.school.urlshortenerservice.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {
}
