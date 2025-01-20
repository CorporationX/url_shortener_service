package faang.school.urlshortenerservice.error;

import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {
}
