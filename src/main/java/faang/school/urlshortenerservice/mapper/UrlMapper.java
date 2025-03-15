package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrlMapper {

    @Mapping(target = "originalUrl", source = "url")
    UrlReadDto toDto(Url url);
}
