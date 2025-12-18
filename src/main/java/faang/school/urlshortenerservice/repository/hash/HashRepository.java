package faang.school.urlshortenerservice.repository.hash;

import java.util.List;

public interface HashRepository {

    List<Long> getUniqueNumbers(int maxRange);

    List<String> getHashBatch(long amount);

    void saveHashes(List<String> hashes);
}