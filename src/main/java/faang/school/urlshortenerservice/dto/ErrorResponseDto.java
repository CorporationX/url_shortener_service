package faang.school.urlshortenerservice.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponseDto {
    private String error;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponseDto(String error, int status) {
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
