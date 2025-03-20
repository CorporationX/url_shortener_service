package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.ApiError;
import faang.school.urlshortenerservice.util.ValidationErrorUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError<String>> handleException(final WebRequest request,
                                                            final Exception exception) {
        return handleThrowable(request, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    /**
     * Maps {@link Throwable} to a {@link ResponseEntity} containing {@link ApiError}.
     *
     * @param request    An object that contains information about the web request.
     * @param httpStatus An HTTP status of the error response.
     * @param throwable  A throwable that has to be processed.
     * @return A {@link ResponseEntity} containing {@link ApiError}.
     */
    public ResponseEntity<ApiError<String>> handleThrowable(final WebRequest request,
                                                            final HttpStatus httpStatus,
                                                            final Throwable throwable) {
        log.error("Exception was thrown", throwable);
        return ResponseEntity.status(httpStatus)
                .body(ApiError.construct(throwable, request));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final @NonNull MethodArgumentNotValidException exception,
            final @NonNull HttpHeaders headers,
            final @NonNull HttpStatusCode status,
            final @NonNull WebRequest request
    ) {
        var errorMessagesPerField = ValidationErrorUtil.getErrorMessagePerField(exception);
        return ResponseEntity.badRequest()
                .body(ApiError.constructValidationError(errorMessagesPerField));
    }
}