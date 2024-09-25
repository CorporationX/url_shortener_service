package faang.school.urlshortenerservice.controller;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
public class ErrorResponse {

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String message;
    private Map<String, String> validationErrors;
}