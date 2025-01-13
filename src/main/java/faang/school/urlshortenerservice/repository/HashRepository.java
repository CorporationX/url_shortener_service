package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<HashEntity, String> {
    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :number)", nativeQuery = true)
    List<Long> getUniqueNumbers(int number);

    @Modifying
    @Query(value = "DELETE FROM hash WHERE hash IN (" +
            "  SELECT hash FROM hash ORDER BY RANDOM() LIMIT :batchSize" +
            ") RETURNING hash", nativeQuery = true)
    List<String> getHashBatch(int batchSize);
}
