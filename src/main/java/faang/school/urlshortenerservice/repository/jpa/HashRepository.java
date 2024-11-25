package faang.school.urlshortenerservice.repository.jpa;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository {

    List<Long> getUniqueNumbers(int number);

    void saveBatch(List<String> hashes);

    List<String> getHashBatch(int number);
}