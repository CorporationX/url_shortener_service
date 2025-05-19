package faang.school.urlshortenerservice.andreev.mapper;

import faang.school.urlshortenerservice.andreev.dto.UrlRequestDto;
import faang.school.urlshortenerservice.andreev.dto.UrlResponseDto;
import faang.school.urlshortenerservice.andreev.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    Url toUrl(UrlRequestDto urlRequestDto);

    UrlResponseDto toUrlResponseDto(Url url);
}
