package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.InsertDataDto;
import faang.school.urlshortenerservice.entity.Hash;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HashMapper {
    InsertDataDto toDto(Hash hash);
    Hash toEntity(InsertDataDto insertDataDto);
}
