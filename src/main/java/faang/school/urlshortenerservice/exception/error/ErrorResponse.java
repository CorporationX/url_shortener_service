package faang.school.urlshortenerservice.exception.error;

import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {
}
