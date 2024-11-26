package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "hash", ignore = true)
    Url toEntity(UrlRequestDto dto);

    UrlResponseDto toResponseDto(Url entity);
}
