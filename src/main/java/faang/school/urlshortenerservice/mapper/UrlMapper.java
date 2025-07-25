package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    UrlRequestDto toRequestDto(Url url);

    UrlResponseDto toResponseDto(Url url);

    Url toEntity(UrlRequestDto urlDto);

    Url toEntity(UrlResponseDto urlDto);
}
