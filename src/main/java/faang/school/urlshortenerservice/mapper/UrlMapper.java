package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    Url toEntity(UrlDto urlDto);
    List<Url> toEntity(List<UrlDto> listUrlDto);

    UrlDto toDto(Url url);
    List<UrlDto> toDto(List<Url>  listUrl);
}
