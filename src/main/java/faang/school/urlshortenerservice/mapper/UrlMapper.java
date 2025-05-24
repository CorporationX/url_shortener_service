package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlRedis;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    UrlRedis toUrlRedis(Url entity);
}
