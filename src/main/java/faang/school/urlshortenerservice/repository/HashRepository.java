package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT NEXTVAL('unique_number_seq') FROM generate_series(1, :batchSize)", nativeQuery = true)
    List<Long> getUniqueNumbers(int batchSize);

    @Query(value = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT :batchSize) RETURNING hash",
            nativeQuery = true)
    List<String> getHashBatchAndDelete(int batchSize);
}
