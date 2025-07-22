package faang.school.urlshortenerservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.UrlRedis;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlRedisMapper {
    UrlRedis toEntity(UrlDto dto);
    UrlDto toDto(UrlRedis entity);

    List<UrlRedis> toEntityList(List<UrlDto> dtos);
    List<UrlDto> toDtoList(List<UrlRedis> entities);

}
