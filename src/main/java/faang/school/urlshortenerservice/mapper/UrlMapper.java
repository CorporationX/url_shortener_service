package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    @Mapping(target = "shortUrl", source = "url.hash", qualifiedByName = "toShortUrlMapping")
    ShortUrlDto toShortUrlDto(Url url, @Context String baseUrl);

    Url toEntity(UrlDto urlDto);

    UrlDto toUrlDto(Url url);

    @Named("toShortUrlMapping")
    default String mapShortUrl(String hash, @Context String baseUrl) {
        return baseUrl + hash;
    }
}
