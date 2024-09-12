package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "string",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    UrlDto toDto(Url url);

    Url toEntity(UrlDto urlDto);
}
