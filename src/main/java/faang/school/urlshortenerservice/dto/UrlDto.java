package faang.school.urlshortenerservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class UrlDto {
    private String longUrl;
}
