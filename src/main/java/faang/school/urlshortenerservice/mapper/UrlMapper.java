package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;

@Mapper
public interface UrlMapper {
    UrlDto toDto(Url url);
    Url toEntity(UrlDto urlDto);
}
