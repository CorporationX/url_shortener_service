package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.model.dto.ShortenUrlRequest;
import faang.school.urlshortenerservice.model.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    Url toUrl(ShortenUrlRequest shortenUrlRequest);
}
