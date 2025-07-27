package faang.school.urlshortenerservice.dto.url;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ResponseShortUrlDto {

    private String urlResponseDto;
    private LocalDateTime createdAt;
}
