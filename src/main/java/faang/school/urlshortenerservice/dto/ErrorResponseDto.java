package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ErrorResponseDto {
    private String code;
    private String message;
    private List<ErrorFieldDto> errors;
}
