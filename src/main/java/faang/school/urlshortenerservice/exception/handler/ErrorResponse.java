package faang.school.urlshortenerservice.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private HttpStatus statusCode;
    private String message;
    private String error;
    private Map<String, String> errors;
}
