package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.entity.UrlEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UrlMapper {
    @Mapping(target = "hash", ignore = true)
    UrlEntity toEntity(UrlDtoRequest urlDtoRequest);
}
