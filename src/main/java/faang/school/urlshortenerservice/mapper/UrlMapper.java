package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    UrlCache toUrlCache(Url url);
}