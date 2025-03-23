package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public class UrlNotFoundException extends RuntimeException {

  private final String serviceName;

  public UrlNotFoundException(String serviceName, String message) {
    super(message);
    this.serviceName = serviceName;
  }
}