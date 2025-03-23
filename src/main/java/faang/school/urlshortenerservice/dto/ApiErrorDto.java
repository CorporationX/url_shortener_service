package faang.school.urlshortenerservice.dto;

import java.util.List;

public record ApiErrorDto(
        String message,
        List<FieldErrorDetail> fieldErrorDetails
) {
}
