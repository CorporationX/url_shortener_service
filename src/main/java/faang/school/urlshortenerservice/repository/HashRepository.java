package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository {

    @Query(value = "select setval('unique_number_seq', (nextval('unique_number_seq') + abs(:count)))")
    List<Long> getUniqueNumbers(int count);


    void saveBatch(List<String> hashes);

    List<String> getHashBatch();
}
