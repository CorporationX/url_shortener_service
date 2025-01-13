package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class UrlExceptionHandler {

  public ResponseEntity<String> handleExceptions(Exception e) {
    String errorMessage = e.getMessage();
    log.error("{}: {}", e.getClass(), errorMessage, e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }
}
