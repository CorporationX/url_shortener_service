package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlsDto;
import faang.school.urlshortenerservice.model.Urls;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UrlsDtoMapper {
    Urls toUrls(UrlsDto urlsDto);

    @Mapping(source = "url", target = "url")
    UrlDto toUrlDtoLongUrl(Urls urls);
}