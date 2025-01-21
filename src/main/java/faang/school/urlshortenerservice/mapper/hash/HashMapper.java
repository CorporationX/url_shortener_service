package faang.school.urlshortenerservice.mapper.hash;

import faang.school.urlshortenerservice.entity.hash.Hash;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashMapper {

    default List<Hash> toEntity(Set<String> hashes) {
        if(hashes == null) {
            return null;
        }
        return hashes.stream()
                .map(Hash::new)
                .toList();
    }
}
