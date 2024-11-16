package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {

    List<Long> getUniqueNumbers(int number);

    void saveBatch(List<String> hashes);

    List<String> getHashBatch(int number);
}