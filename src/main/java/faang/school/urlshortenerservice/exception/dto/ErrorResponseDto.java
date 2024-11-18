package faang.school.urlshortenerservice.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResponseDto {
    private final String error;
    private final String message;
    private final int status;
    private final String path;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;
}
