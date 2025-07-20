package faang.school.urlshortenerservice.exception;

import lombok.Data;

@Data
public class UrlNotFoundException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "URL with hash '%s' not found";

    public UrlNotFoundException(String hash) {
        super(String.format(MESSAGE_TEMPLATE, hash));
    }
}