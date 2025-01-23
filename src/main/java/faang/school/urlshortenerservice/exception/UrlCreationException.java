package faang.school.urlshortenerservice.exception;

/**
 * Исключение, которое выбрасывается при возникновении ошибки во время создания URL.
 */
public class UrlCreationException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина ошибки (исключение, которое вызвало эту ошибку).
     */
    public UrlCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
