package faang.school.urlshortenerservice.common;

import faang.school.urlshortenerservice.controller.UrlController;
import faang.school.urlshortenerservice.dto.ApiExceptionDto;
import faang.school.urlshortenerservice.exception.CleanHashException;
import faang.school.urlshortenerservice.exception.HashCreatedException;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static faang.school.urlshortenerservice.dto.ApiExceptionDto.ErrorType.BUSINESS_ERROR;
import static faang.school.urlshortenerservice.dto.ApiExceptionDto.ErrorType.SERVER_ERROR;

@RestControllerAdvice(assignableTypes = {UrlController.class})
@Slf4j
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(InvalidUrlException.class)
    public ApiExceptionDto invalidUrlException(final InvalidUrlException e) {
        log.error(e.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto();
        apiExceptionDto.setMessage(e.getMessage());
        apiExceptionDto.setTimestamp(System.currentTimeMillis());
        apiExceptionDto.setErrorType(BUSINESS_ERROR);
        return apiExceptionDto;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(HashCreatedException.class)
    public ApiExceptionDto hashCreatedException(final HashCreatedException e) {
        log.error(e.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto();
        apiExceptionDto.setMessage(e.getMessage());
        apiExceptionDto.setTimestamp(System.currentTimeMillis());
        apiExceptionDto.setErrorType(SERVER_ERROR);
        return apiExceptionDto;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(CleanHashException.class)
    public ApiExceptionDto cleanHashException(final CleanHashException e) {
        log.error(e.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto();
        apiExceptionDto.setMessage(e.getMessage());
        apiExceptionDto.setTimestamp(System.currentTimeMillis());
        apiExceptionDto.setErrorType(SERVER_ERROR);
        return apiExceptionDto;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ApiExceptionDto exception(final Exception e) {
        log.error(e.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto();
        apiExceptionDto.setMessage(e.getMessage());
        apiExceptionDto.setTimestamp(System.currentTimeMillis());
        apiExceptionDto.setErrorType(SERVER_ERROR);
        return apiExceptionDto;
    }
}
