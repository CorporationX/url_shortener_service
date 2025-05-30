package faang.school.urlshortenerservice.ExceptionHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class UrlNotFoundException extends RuntimeException {
    private Errors error;
    public UrlNotFoundException(String message) {
        super(message);
        this.error = Errors.NOT_FOUND;
    }
}
