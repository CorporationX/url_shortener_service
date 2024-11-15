package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    @Mapping(source = "url", target = "longUrl")
    Url toEntity(UrlDto urlDto);
}
