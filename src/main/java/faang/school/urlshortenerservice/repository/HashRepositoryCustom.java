package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepositoryCustom {
    List<Long> getUniqueNumbers(int count);

    void saveHashes(List<String> hashes);

    List<String> getHashBatch(int count);
}

