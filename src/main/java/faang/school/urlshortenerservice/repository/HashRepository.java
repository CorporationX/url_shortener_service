package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value =
        """
        SELECT
            nextval('unique_number_seq')
        FROM
            generate_series(1, :batchSize)
        """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("batchSize") long batchSize);

    @Modifying
    @Query(value = """
        DELETE FROM hash
            WHERE hash IN (
                SELECT hash
                FROM hash
                ORDER BY RANDOM()
                LIMIT :batchSize
                FOR UPDATE SKIP LOCKED
            )
            RETURNING hash
        """, nativeQuery = true)
    List<Hash> getHashBatch(@Param("batchSize") long batchSize);

    @Modifying
    @Query(value = "INSERT INTO hash (hash) SELECT unnest(CAST(:hashes AS VARCHAR[]))", nativeQuery = true)
    void saveBatch(@Param("hashes") String[] hashes);
}
