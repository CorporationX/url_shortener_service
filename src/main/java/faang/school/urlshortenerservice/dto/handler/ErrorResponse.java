package faang.school.urlshortenerservice.dto.handler;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String serviceName;
    private String message;
    private Map<String, String> details;
    private int status;
}
