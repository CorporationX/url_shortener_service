package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?1)")
    List<Long> getUniqueNumbers(long maxRange);

    @Query(nativeQuery = true,
            value = """
           DELETE FROM hash 
           WHERE hash IN (SELECT hash FROM hash ORDER BY hash LIMIT :batchSize)
           RETURNING hash
           """)
    List<Hash> getHashBatch(@Param("batchSize") int batchSize);
}
