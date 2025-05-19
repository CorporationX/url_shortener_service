package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {
    private String message;
    private int status;
    private String method;
    private String path;
    private String timestamp;
}
