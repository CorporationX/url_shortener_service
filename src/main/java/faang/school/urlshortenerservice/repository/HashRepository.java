package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {

    List<Long> getUniqueNumbers(int n);

    void saveBatch(List<String> hashes);

    List<String> getHashBatch();

    Long getHashCount();
}
