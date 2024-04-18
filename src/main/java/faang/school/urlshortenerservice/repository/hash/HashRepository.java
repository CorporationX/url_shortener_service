package faang.school.urlshortenerservice.repository.hash;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface HashRepository {

    List<Long> getUniqueNumbers(int amount);

    void save(Set<String> hashes);

    ConcurrentLinkedQueue<String> getHashBatch();

    Long count();
}
