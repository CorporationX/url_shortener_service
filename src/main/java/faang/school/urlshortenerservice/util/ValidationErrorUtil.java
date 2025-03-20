package faang.school.urlshortenerservice.util;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for processing validation errors.
 */
public final class ValidationErrorUtil {

    private ValidationErrorUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * The default error message to be used when a field error message is not available.
     */
    public static final String DEFAULT_FIELD_ERROR_MESSAGE = "Invalid value";

    /**
     * Retrieves all error messages for each field from the given {@link MethodArgumentNotValidException}.
     * This method extracts field errors from the exception's binding result and groups them by field name.
     * For each field, it collects all associated error messages into a list.
     *
     * @param exception The {@code MethodArgumentNotValidException} containing validation errors.
     * @return A {@link Map} where keys represent field names and values are lists of error messages.
     *         If no error message is provided for a field, a default message is used.
     * @throws NullPointerException if {@code exception} is {@code null}.
     */
    public static Map<String, List<String>> getErrorMessagePerField(final MethodArgumentNotValidException exception) {
        return exception.getBindingResult()
                .getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                fieldError -> Objects.isNull(fieldError.getDefaultMessage())
                                        ? DEFAULT_FIELD_ERROR_MESSAGE
                                        : fieldError.getDefaultMessage(),
                                Collectors.toList())
                ));
    }
}