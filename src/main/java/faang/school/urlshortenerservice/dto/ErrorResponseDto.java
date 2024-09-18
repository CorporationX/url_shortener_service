package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private int code;
    private String message;
    private Map<String, String> details;

    public ErrorResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }
}