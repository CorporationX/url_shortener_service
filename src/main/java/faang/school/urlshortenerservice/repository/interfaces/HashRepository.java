package faang.school.urlshortenerservice.repository.interfaces;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository {

    List<Long> getUniqueNumbers(int n);

    void save(List<String> hashes);

    List<String> getHashBatch();
}