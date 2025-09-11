package faang.school.urlshortenerservice.exception;

public class UrlCleanupException extends RuntimeException {

  public UrlCleanupException(String message) {
    super(message);
  }

  public UrlCleanupException(String message, Throwable cause) {
    super(message, cause);
  }
}