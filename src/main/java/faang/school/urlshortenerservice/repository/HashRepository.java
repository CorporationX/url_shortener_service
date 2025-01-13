package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {
    List<Long> getUniqueNumbers(long amount);

    void save(List<String> hashes);

    List<String> getHashBatch(long amount);
}
