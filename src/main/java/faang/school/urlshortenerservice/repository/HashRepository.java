package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :countNumbers)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("countNumbers") int countNumbers);

    @Query(value = """
            DELETE FROM hash WHERE hash IN (
                SELECT hash FROM hash ORDER BY random() LIMIT :batchSize
            )
            RETURNING hash;
            """, nativeQuery = true)
    List<Hash> getHashBatch(@Param("batchSize") int batchSize);
}
