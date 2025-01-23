package faang.school.urlshortenerservice.exception;

/**
 * Исключение, которое выбрасывается, если URL не найден в кэше или базе данных.
 */
public class UrlNotFoundException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public UrlNotFoundException(String message) {
        super(message);
    }
}
