package faang.school.urlshortenerservice.repository.jpa;

import java.util.List;

public interface HashRepository {

    List<Long> getUniqueNumbers(int number);

    void saveBatch(List<String> hashes);

    List<String> getHashBatch(int number);
}