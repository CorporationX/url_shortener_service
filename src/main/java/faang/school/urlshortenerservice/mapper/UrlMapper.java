package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;

public interface UrlMapper {
    UrlDto toDto (Url url);

    Url toEntity(UrlDto urlDto);
}
