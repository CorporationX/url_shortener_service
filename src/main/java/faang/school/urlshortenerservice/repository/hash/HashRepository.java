package faang.school.urlshortenerservice.repository.hash;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository {

    List<Long> getUniqueNumbers(int batchSize);
    void saveHashes(List<String> hashes);
    List<String> getHashBatch(int batchSize);
}
