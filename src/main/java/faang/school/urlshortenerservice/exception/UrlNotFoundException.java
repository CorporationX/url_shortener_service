package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String hash) {
        super("Оригинальная ссылка не найдена для хэша: " + hash);
    }
}