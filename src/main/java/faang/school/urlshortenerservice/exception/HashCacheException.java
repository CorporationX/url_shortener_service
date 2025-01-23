package faang.school.urlshortenerservice.exception;

/**
 * Исключение, которое выбрасывается при возникновении ошибки во время работы с кэшем хэшей.
 */
public class HashCacheException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public HashCacheException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с указанным сообщением и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина ошибки (исключение, которое вызвало эту ошибку).
     */
    public HashCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
