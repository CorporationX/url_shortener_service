package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.GeneralFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

//@RestControllerAdvice
public class UrlExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> handleMethodNotValidException(MethodArgumentNotValidException validException) {
//        return validException.getBindingResult().getAllErrors().stream()
//                .collect(Collectors.toMap(
//                        error -> ((FieldError) error).getField(),
//                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
//                ));
//    }
//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String handleInternalSystem(Throwable t){
//        return t.getMessage();
//    }
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public GeneralFormatException handleRuntimeException(Exception e) {
//        return new GeneralFormatException(e.getMessage());
//    }
}
