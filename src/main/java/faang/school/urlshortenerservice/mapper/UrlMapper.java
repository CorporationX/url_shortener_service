package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.DTO.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    UrlDto toDto(Url url);
    Url toEntity(UrlDto urlDto);
}