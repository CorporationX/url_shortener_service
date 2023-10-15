package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDTO;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    UrlDTO toDto(Url url);
    Url toUrl(UrlDTO dto);
}