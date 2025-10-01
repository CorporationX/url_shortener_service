package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ApiExceptionDto {
    private String message;
    private long timestamp;
    private ErrorType errorType;

    @AllArgsConstructor
    public enum ErrorType {
        BUSINESS_ERROR("R_01", "Business Error"),
        SERVER_ERROR("S_01", "Internal server error");

        private String code;
        private String description;
    }
}
