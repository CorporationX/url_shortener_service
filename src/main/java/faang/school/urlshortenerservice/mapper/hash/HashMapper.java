package faang.school.urlshortenerservice.mapper.hash;

import faang.school.urlshortenerservice.dto.hash.HashDto;
import faang.school.urlshortenerservice.entity.Hash;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashMapper {

    Hash toEntity(HashDto dto);
    HashDto toHashDto(Hash hash);

    List<Hash> toEntityList(List<HashDto> dtoList);
}
