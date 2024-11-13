package faang.school.urlshortenerservice.mapper.url;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    Url toEntity(UrlDto dto);
    UrlDto toUrlDto(Url entity);
}
