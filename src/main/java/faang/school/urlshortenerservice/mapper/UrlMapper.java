package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.model.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    @Mapping(source = "createdAt", target = "createTime")
    UrlResponseDto toDto(Url url);

    @Mapping(source = "createTime", target = "createdAt")
    Url toEntity(UrlResponseDto dto);
}
