package faang.school.urlshortenerservice.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ErrorResponseFactory {

    public static ErrorResponse create(Exception e, HttpServletRequest request, HttpStatus status) {
        return ErrorResponse.builder()
                .url(safeUrl(request))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(safeMessage(e, status))
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse create(MethodArgumentNotValidException e, HttpServletRequest req, HttpStatus status) {
        Map<String, List<String>> errors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                fe -> fe.getDefaultMessage() != null && !fe.getDefaultMessage().isBlank()
                                        ? fe.getDefaultMessage() : "Unknown error",
                                Collectors.toList()
                        )
                ));

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(safeUrl(req))
                .status(status.value())
                .error(status.getReasonPhrase())
                .details(errors)
                .message("Validation failed")
                .build();
    }

    public static ErrorResponse create(ConstraintViolationException e, HttpServletRequest request, HttpStatus status) {
        Map<String, List<String>> errors = e
                .getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        constraintViolation -> constraintViolation.getPropertyPath().toString(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                v -> v.getMessage() != null && !v.getMessage().isBlank()
                                        ? v.getMessage() : "Unknown error",
                                Collectors.toList()
                        )
                ));

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(safeUrl(request))
                .status(status.value())
                .error(status.getReasonPhrase())
                .details(errors)
                .message("Validation failed")
                .build();
    }

    private static String safeMessage(Throwable e, HttpStatus status) {
        if (status.is5xxServerError()) {
            return "Internal server error. Something went wrong.";
        }

        return Optional.ofNullable(e)
                .map(Throwable::getMessage)
                .filter(s -> !s.isBlank())
                .orElse("Unexpected error occurred");
    }

    private static String safeUrl(HttpServletRequest req) {
        if (req == null) {
            return "N/A";
        }

        String uri = Optional.ofNullable(req.getRequestURI()).orElse("");
        String query = req.getQueryString();

        if (query == null || query.isBlank()) {
            return uri.isBlank() ? "N/A" : uri;
        }

        return uri.isBlank() ? "N/A" : uri + "?" + query;
    }
}
