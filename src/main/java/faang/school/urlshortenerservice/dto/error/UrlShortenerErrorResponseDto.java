package faang.school.urlshortenerservice.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlShortenerErrorResponseDto {
    private String errorMsg;
    private LocalDateTime timestamp;
    private int codeResponse;
}
