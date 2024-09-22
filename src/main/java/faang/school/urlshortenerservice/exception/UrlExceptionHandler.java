package faang.school.urlshortenerservice.exception;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class UrlExceptionHandler {

    private ErrorResponse response;

    @ExceptionHandler(FeignException.FeignClientException.class)
    public ResponseEntity<ErrorResponse> handlerFeignClientException(FeignException.FeignClientException e){
        extracted(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerException(Exception e){
        extracted(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UrlValidationException.class)
    public ResponseEntity<ErrorResponse> handlerUrlValidationException(Exception e){
        extracted(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private void extracted(String message) {
        String formattedDateTime = getString();
        response = new ErrorResponse(String.format("%s %s", formattedDateTime, message));
        log.info(message, response.getMessage());
    }

    private static String getString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}
