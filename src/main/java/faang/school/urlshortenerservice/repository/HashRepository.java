package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true,
            value = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?1)")
    List<Long> getUniqueNumbers(long maxRange);

    @Query(nativeQuery = true,
            value = """
               DELETE FROM hashes 
               WHERE hash IN (SELECT hash FROM hashes ORDER BY hash LIMIT :batchSize)
               RETURNING hash
               """)
    List<Hash> getHashBatch(int batchSize);
}
