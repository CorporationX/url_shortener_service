package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {

    List<Long> getUniqueNumbers(Long size);

    void saveBatch(List<String> hashes);

    List<String> getHashBatch(Long size);
}