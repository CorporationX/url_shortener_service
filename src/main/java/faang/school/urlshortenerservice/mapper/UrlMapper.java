package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface UrlMapper {
    UrlDto toUrlDto(Url url);

    Url toUrl(UrlDto urlDto);
}
