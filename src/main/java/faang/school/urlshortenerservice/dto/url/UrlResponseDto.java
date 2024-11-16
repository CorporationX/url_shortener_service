package faang.school.urlshortenerservice.dto.url;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UrlResponseDto {

    private String url;
}
