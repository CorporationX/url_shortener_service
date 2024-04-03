package faang.school.urlshortenerservice.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status;
    private String message;
}
