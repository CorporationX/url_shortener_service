package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface UrlMapper {

    @Mapping(source = "hash", target = "hash")
    @Mapping(source = "dto.targetUrl", target = "url")
    Url toEntity(CreateUrlDto dto, String hash);
}
