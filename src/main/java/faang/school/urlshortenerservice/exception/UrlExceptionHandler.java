package faang.school.urlshortenerservice.exception;


import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Component
@ControllerAdvice
public class UrlExceptionHandler {

//    @ExceptionHandler(UrlNotFoundException.class)
//    public ResponseEntity<Response> handleException(UrlNotFoundException e) {
//        Response response = new Response(e.getMessage());
//    }
}
