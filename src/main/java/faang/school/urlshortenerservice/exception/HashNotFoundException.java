package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.exception.annotation.HttpStatusError;
import org.springframework.http.HttpStatus;

@HttpStatusError(
        value = HttpStatus.NOT_FOUND,
        message = "URL не найден в кэше"
)
public class HashNotFoundException extends RuntimeException {
    public HashNotFoundException(String hash) {
        super(hash);
    }
}
