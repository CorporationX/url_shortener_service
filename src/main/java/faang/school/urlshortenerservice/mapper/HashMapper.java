package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.entity.Hash;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@Mapper(componentModel = "spring")
public interface HashMapper {

    default Hash map(String value) {
        Hash hash = new Hash();
        hash.setHash(value);
        return hash;
    }

    List<Hash> toEntity(List<String> hashes);
    List<Hash> toHashFromCompletableFutureHash(CompletableFuture<List<Hash>> hashes);
    List<String> toListStringFromHash(List<Hash> hashes);
}
