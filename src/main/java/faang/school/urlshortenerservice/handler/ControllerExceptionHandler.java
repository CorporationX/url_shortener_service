package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.response.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//    @ExceptionHandler(AccountNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponseDto handleAccountNotFound(AccountNotFoundException e) {
//        log.error("Account number not found error:", e);
//        return new ErrorResponseDto(
//                HttpStatus.NOT_FOUND.name(),
//                "Invalid account number was provided.",
//                e.getMessage(),
//                LocalDateTime.now().format(formatter)
//        );
//    }
//
//    @ExceptionHandler(AccountStateException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponseDto handleAccountState(AccountStateException e) {
//        log.error("Account state error:", e);
//        return new ErrorResponseDto(
//                HttpStatus.CONFLICT.name(),
//                "Invalid status for account.",
//                e.getMessage(),
//                LocalDateTime.now().format(formatter)
//        );
//    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        log.error("Some error occurred.", e);
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something went wrong.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }
}
