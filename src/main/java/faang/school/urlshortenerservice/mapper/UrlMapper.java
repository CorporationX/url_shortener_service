package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.url.SaveUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrlMapper {
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Url toEntity(SaveUrlDto saveUrlDto);
}
