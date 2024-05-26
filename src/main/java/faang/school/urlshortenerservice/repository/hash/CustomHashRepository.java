package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface CustomHashRepository {
    List<Long> getUniqueNumbers(long maxRange);
    void save(List<String> hashes);
    List<Hash> getHashBatch(long amount);

}
