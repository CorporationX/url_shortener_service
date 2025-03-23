package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {
    List<Long> getUniqueNumbers(int fetchSize);

    void saveAll(List<String> hashes);

    List<String> getHashBatch(int fetchSize);
}
