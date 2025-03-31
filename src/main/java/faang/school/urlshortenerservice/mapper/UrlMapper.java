package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.event.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    UrlReadDto toDto(Url entity);
}
