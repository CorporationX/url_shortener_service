package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.entity.Hash;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashMapper {

    default Hash map(String value) {
        Hash hash = new Hash();
        hash.setHash(value);
        return hash;
    }

    default String map(Hash hash) {
        return hash.getHash();
    }

    List<Hash> toEntity(List<String> hashes);
    List<String> toListStringFromHash(List<Hash> hashes);
}
