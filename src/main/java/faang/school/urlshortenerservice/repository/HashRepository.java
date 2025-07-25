package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {
    List<Long> getUniqueNumbers(int n);
    boolean save(List<String> hashes);
    List<String> getHashBatch();
    Long countHashes();
}
