package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String serviceName;
    private String className;
    private int status;
    private String message;
    private Map<String, Object> additionalAttributes;
}
