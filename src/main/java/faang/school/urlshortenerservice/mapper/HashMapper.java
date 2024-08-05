package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.model.Hash;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface HashMapper {

    @Mapping(target = "hash", source = "value")
    Hash map(String value);

    List<Hash> toEntities(List<String> values);
}
