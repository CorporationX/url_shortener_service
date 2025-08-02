package faang.school.urlshortenerservice.controller.handler;

import faang.school.urlshortenerservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

public enum ErrorReasons {
    ENTITY_NOT_FOUND(EntityNotFoundException.class, "The required object was not found."),
    VALIDATION_ERROR(DataValidationException.class, "Incorrectly made request."),
    UNKNOWN_ERROR(Exception.class, "Something went wrong.");

    private final Class<? extends Exception> exceptionType;
    @Getter
    private final String message;

    ErrorReasons(Class<? extends Exception> exceptionType, String message) {
        this.exceptionType = exceptionType;
        this.message = message;
    }

    public static String getMessageFor(Exception exception) {
        for (ErrorReasons reason : values()) {
            if (reason.exceptionType.isInstance(exception)) {
                return reason.getMessage();
            }
        }
        return UNKNOWN_ERROR.getMessage();
    }
}