package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String hash) {
        super("URL адрес не найден для хэша: " + hash);
    }
}
