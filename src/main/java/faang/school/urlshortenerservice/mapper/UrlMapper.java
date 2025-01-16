package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UrlMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UrlDto urlToUrlDto(Url url);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Url urlDtoToUrl(UrlDto urlDto);
}
