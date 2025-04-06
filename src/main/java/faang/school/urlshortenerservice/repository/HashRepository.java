package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(value = """
            SELECT nextval('unique_number_seq') from generate_series(1, :maxRange)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("maxRange") int maxRange);

    @Modifying
    @Query(value = """
            DELETE FROM hash
            WHERE hash IN (SELECT hash FROM hash LIMIT :batchSize)
            RETURNING *;
            """, nativeQuery = true)
    List<Hash> getHashBatch(@Param("batchSize") int batchSize);
}
