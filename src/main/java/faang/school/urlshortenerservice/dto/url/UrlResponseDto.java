package faang.school.urlshortenerservice.dto.url;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.net.URI;

@Getter
@AllArgsConstructor
@Builder
public class UrlResponseDto {

    private URI urlResponseDto;
}
