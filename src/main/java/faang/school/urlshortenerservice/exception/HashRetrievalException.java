package faang.school.urlshortenerservice.exception;

/**
 * Исключение, которое выбрасывается при возникновении ошибки во время получения хэшей.
 */
public class HashRetrievalException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public HashRetrievalException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с указанным сообщением и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина ошибки (исключение, которое вызвало эту ошибку).
     */
    public HashRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
