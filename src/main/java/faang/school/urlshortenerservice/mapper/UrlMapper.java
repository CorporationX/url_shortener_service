package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrlMapper {

    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Url toUrl(UrlDto urlDto);

    UrlDto toUrlDto(Url url);
}
