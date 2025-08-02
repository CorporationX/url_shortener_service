package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HashRepository {

    List<Long> getUniqueNumbers(int n);

    void saveAll(List<String> hashes);

    List<Hash> getHashBatch(int batchSize);
}
