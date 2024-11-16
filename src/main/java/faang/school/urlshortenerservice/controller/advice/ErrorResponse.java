package faang.school.urlshortenerservice.controller.advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class ErrorResponse {
    private final int statusCode;
    private final String message;
    private final Map<String, String> errors;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(HttpStatus status, String message, Exception ex) {
        StackTraceElement stackTrace = ex.getStackTrace()[0];
        this.statusCode = status.value();
        this.message = message;
        this.errors = Map.of("fileName", stackTrace.getFileName(),
                "className", stackTrace.getClassName(),
                "methodName", stackTrace.getMethodName(),
                "lineNumber", String.valueOf(stackTrace.getLineNumber()));
    }

    public ErrorResponse(HttpStatus status, String message, Map<String, String> errors) {
        this.errors = errors;
        this.statusCode = status.value();
        this.message = message;
    }
}
