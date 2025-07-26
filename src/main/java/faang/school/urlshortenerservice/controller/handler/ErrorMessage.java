package faang.school.urlshortenerservice.controller.handler;

public enum ErrorMessage {
    URL_NOT_FOUND("Url was not found."),
    BAD_REQUEST("Incorrectly made request."),
    INTERNAL_ERROR("Something get wrong.");

    public final String errorMessage;

    ErrorMessage(String message) {
        this.errorMessage = message;
    }
} 