package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.entity.Hash;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface HashMapper {

    HashDto toDto(Hash hash);
}