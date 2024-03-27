package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCash;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    UrlCash toUrlCash(Url url);

}