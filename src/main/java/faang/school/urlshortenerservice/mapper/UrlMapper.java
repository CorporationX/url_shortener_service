package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlMapper {
    String toString(UrlDto urlDto);
}
