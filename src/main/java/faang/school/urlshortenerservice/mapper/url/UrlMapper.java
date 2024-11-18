package faang.school.urlshortenerservice.mapper.url;

import faang.school.urlshortenerservice.dto.url.ResponseUrlBody;
import faang.school.urlshortenerservice.entity.url.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    @Mapping(target = "shortUrl", expression = "java(shortLink + url.getHash())")
    ResponseUrlBody toResponseBody(Url url, String shortLink);
}
