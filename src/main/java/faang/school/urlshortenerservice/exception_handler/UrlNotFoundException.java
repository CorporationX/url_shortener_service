package faang.school.urlshortenerservice.exception_handler;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String hash) {
        super("URL не найден для хэша: " + hash);
    }
}
