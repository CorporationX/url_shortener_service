package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {
    List<Long> getUniqueNumbers(long n);
    List<String> pollHashBatch(long n);
    void saveBatch(List<String> hashes);
    int getHashesNumber();
}
