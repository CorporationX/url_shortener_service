package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.document.ShortUrl;
import faang.school.urlshortenerservice.dto.short_url.CreateShortUrlDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ShortUrlMapper {
    ShortUrl toShortUrl(CreateShortUrlDto dto);
}
