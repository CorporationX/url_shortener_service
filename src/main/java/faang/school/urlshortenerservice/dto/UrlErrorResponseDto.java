package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlErrorResponseDto {
    private String errorMsg;
    private LocalDateTime timestamp;
    private int codeResponse;
}