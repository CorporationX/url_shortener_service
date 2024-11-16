package faang.school.urlshortenerservice.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private int status;
    private String error;
    private String path;

}
