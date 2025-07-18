package faang.school.urlshortenerservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.model.Hash;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashMapper {
    Hash toEntity(HashDto dto);
    HashDto toDto(Hash entity);

    List<Hash> toEntityList(List<HashDto> dtos);
    List<HashDto> toDtoList(List<Hash> entities);
}
