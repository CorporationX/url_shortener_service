package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    @Mapping(target = "hash", expression = "java(hash)")
    Url toEntity(UrlDto urlDto, @Context String hash);
}
