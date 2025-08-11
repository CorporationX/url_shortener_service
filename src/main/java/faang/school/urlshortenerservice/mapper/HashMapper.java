package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.model.Hash;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashMapper {
    Hash toEntity(HashDto dto);
    HashDto toDto(Hash entity);

    List<Hash> toEntityList(List<HashDto> dtos);
    List<HashDto> toDtoList(List<Hash> entities);
}
