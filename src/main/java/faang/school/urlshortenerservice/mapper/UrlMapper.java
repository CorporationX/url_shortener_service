package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    UrlResponse toUrlResponse(Url url);
}
