package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.CreateUrlRequest;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UrlMapper {
    Url toEntity(CreateUrlRequest dto, String hash);
}
