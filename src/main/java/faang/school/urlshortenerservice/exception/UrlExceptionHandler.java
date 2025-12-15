package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

	@ExceptionHandler(UrlNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> handleUrlNotFound(UrlNotFoundException ex) {
		log.warn("URL not found", ex);
		return Map.of(
				"error", "Short URL not found",
				"hash", ex.getMessage()
		);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleGeneralException(Exception ex) {
		log.error("Unexpected error occurred", ex);
		return Map.of(
				"error", "Internal server error",
				"message", "An unexpected error occurred"
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.warn("Validation failed: {}, details: {}",
				ex.getMessage(), ex.getBindingResult().getAllErrors(), ex);
		return ex.getBindingResult().getAllErrors().stream()
				.map(error -> (FieldError) error)
				.collect(Collectors.toMap(
						fieldError -> fieldError.getField(),
						fieldError -> fieldError.getDefaultMessage()
				));
	}
}