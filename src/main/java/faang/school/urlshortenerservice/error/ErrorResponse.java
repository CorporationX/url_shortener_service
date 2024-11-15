package faang.school.urlshortenerservice.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private int status;
    private String error;
    private String path;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
