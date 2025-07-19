package faang.school.urlshortenerservice.repository.hash;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository {

    List<Long> getUniqueNumbers(int count);

    void save(List<String> hashes);

    List<String> getHashBatch();
}
