package faang.school.urlshortenerservice.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository {

    List<Long> getUniqueNumbers(long n);

    void save(List<String> hashes);

    List<String> getHashBatch();
}
