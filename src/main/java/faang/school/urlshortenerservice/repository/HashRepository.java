package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = "DELETE FROM hash WHERE " +
            "hash IN (SELECT hash FROM hash LIMIT :batchSize) " +
            "RETURNING hash")
    @Transactional
    List<Hash> getHashBatch(long batchSize);


    @Query(nativeQuery = true,
            value = "SELECT nextval('unique_number_seq') AS num FROM generate_series(1,:n)")
    List<Long> getUniqueNumbers(long n);
}
