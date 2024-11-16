package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {

    List<Long> getUniqueNumbers(int number);

    void save(List<String> batch);

    List<String> getHashBatch();
}