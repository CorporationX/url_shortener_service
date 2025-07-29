package faang.school.urlshortenerservice.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private String error;
    private int status;
    private String path;
    private String method;
    private String timestamp;
    private String exception;
    private String errorId;
}
