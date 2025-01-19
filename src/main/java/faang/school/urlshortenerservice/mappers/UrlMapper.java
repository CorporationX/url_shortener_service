package faang.school.urlshortenerservice.mappers;

import faang.school.urlshortenerservice.DTO.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    UrlDto toDto(Url url);
    Url toEntity(UrlDto urlDto);
}
