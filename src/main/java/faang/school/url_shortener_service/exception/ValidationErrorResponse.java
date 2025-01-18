package faang.school.url_shortener_service.exception;

import java.util.List;

public record ValidationErrorResponse(int status, String message, List<FieldErrorDetail> errors) {
}