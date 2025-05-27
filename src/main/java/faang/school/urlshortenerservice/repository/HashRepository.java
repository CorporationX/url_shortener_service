package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long>, BatchHashRepository {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :count)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int count);

    @Modifying
    @Query(value = """
        WITH batch AS (
            SELECT hash
            FROM hash
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
        )
        DELETE FROM hash
        WHERE hash IN (SELECT hash FROM batch)
        RETURNING hash
        """, nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int count);
}
