package faang.school.urlshortenerservice.exception;

/**
 * Исключение, которое выбрасывается при возникновении ошибки во время генерации хэшей.
 */
public class HashGenerationException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public HashGenerationException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с указанным сообщением и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина ошибки (исключение, которое вызвало эту ошибку).
     */
    public HashGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
