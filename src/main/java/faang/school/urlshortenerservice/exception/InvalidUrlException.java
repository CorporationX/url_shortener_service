package faang.school.urlshortenerservice.exception;

public class InvalidUrlException extends RuntimeException {

  public InvalidUrlException(String message) {
    super(message);
  }

}
