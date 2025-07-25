package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.url.CreateUrlDto;
import faang.school.urlshortenerservice.dto.url.ResponseShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;

public class UrlMapper {

    public static Url urlCreateDtoToUrl(CreateUrlDto urlDto) {
        return Url.builder()
                .url(urlDto.getUrlDto())
                .build();
    }

    public static ResponseShortUrlDto responseUrlToDto(Url url) {
        return ResponseShortUrlDto.builder()
                .urlResponseDto(url.getUrl())
                .createdAt(url.getCreatedAt())
                .build();
    }
}
