package faang.school.urlshortenerservice.mapper;


import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    @Mapping(target = "link", source = "hash")
    UrlDto toDto(Url url);
}
