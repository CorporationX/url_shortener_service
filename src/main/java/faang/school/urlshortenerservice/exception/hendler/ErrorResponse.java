package faang.school.urlshortenerservice.exception.hendler;

import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {
}
