package faang.school.urlshortenerservice.exeption;

import lombok.Getter;

import java.util.Map;

@Getter
public class DataValidationException extends RuntimeException {
    private final Map<String, String> errorDetails;
    public DataValidationException(String message, Map<String, String> errorDetails) {
        super(message);
        this.errorDetails = errorDetails;
    }
}
