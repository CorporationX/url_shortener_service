package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlMapper {
    UrlResponseDto toUrlResponseDto(Url url);
}
