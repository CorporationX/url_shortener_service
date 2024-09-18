package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.model.Hash;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashMapper {
    HashDto hashToHashDto(Hash hash);
}
