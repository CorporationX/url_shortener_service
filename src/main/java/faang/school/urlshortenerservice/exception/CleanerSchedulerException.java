package faang.school.urlshortenerservice.exception;

/**
 * Исключение, которое выбрасывается при возникновении ошибки во время работы планировщика очистки.
 */
public class CleanerSchedulerException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public CleanerSchedulerException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с указанным сообщением и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина ошибки (исключение, которое вызвало эту ошибку).
     */
    public CleanerSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
