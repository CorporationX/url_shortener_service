package faang.school.urlshortenerservice.exception;

/**
 * Исключение, которое выбрасывается при возникновении ошибки во время инициализации кэша.
 */
public class CacheInitializationException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public CacheInitializationException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с указанным сообщением и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина ошибки (исключение, которое вызвало эту ошибку).
     */
    public CacheInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
