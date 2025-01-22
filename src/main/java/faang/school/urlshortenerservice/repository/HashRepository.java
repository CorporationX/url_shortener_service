package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {
    List<Long> getUniqueNumbers(long amount);

    void saveAll(List<String> hashes);

    List<String> getHashBatchAndDelete(long amount);
}
