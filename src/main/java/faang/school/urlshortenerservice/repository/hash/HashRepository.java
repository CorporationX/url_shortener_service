package faang.school.urlshortenerservice.repository.hash;

import java.util.List;

public interface HashRepository {
    List<Long> getUniqueNumbers(int n);
    void save(List<String> hashes);
    List<String> getHashBatch(int n);
    long count();
}