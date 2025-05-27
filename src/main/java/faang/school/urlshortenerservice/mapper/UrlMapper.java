package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    Url toUrl(UrlRequestDto urlRequestDto);

    @Mapping(source = "hash", target = "hash")
    @Mapping(source = "url", target = "url")
    UrlResponseDto toUrlResponseDto(Url url);
}
