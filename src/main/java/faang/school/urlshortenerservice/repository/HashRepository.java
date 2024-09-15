package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashRepository {
    List<Long> getUniqueNumbers(long n);
    List<String> getHashBatch(long n);
    void saveBatch(List<String> hashes);
}
