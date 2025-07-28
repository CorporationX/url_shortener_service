package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.response.ErrorResponseDto;
import faang.school.urlshortenerservice.exceptions.IllegalHashLength;
import faang.school.urlshortenerservice.exceptions.IllegalIdForHash;
import faang.school.urlshortenerservice.exceptions.NoAvailableHashesFound;
import faang.school.urlshortenerservice.exceptions.NonExistingHashProvided;
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

    @ExceptionHandler(NonExistingHashProvided.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleNonExistingHashProvidedException(NonExistingHashProvided e) {
        log.error("Provided hash for original url retrieving isn't present in both Cache/DB:", e);
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Non-existing hash provided.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    // Как будто пользователю похер? Он ведь даже этого никогда не увидит?
    @ExceptionHandler(IllegalHashLength.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleIllegalHashLengthException(IllegalHashLength e) {
        log.error("Hash generation resulted in error, provided or resulted hash length was incorrect:", e);
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Service stumbled into an error during request procession, retry later.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    // Как будто пользователю похер?
    @ExceptionHandler(IllegalIdForHash.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleIllegalIdForHashException(IllegalIdForHash e) {
        log.error("Id provided for hash generation must be non-negative:", e);
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Service stumbled into an error during request procession, retry later.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(NoAvailableHashesFound.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleNoAvailableHashesFoundException(NoAvailableHashesFound e) {
        log.error("Service run out of hashes in both database and cache:", e);
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Service can't provide hash currently, try again later.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

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
