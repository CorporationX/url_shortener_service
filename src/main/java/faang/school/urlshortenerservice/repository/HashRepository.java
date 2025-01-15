package faang.school.urlshortenerservice.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Object, Long> {

    @Value("${app.hash.batchSize}")
    int batchSize = 0;

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :batchSize)
            """)
    List<Long> getUniqueNumbers(@Param("batchSize") int batchSize);

    @Query(nativeQuery = true, value = """
                WITH deleted_rows AS (
                    DELETE FROM hash
                    WHERE id IN (
                        SELECT id
                        FROM hash
                        ORDER BY RANDOM()
                        LIMIT :batchSize
                    )
                    RETURNING value
                )
                SELECT value FROM deleted_rows
            """)
    List<String> getHashBatch(@Param("batchSize") int batchSize);

    @Query(nativeQuery = true, value = """
                INSERT INTO hash (value)
                VALUES (:hashes)
            """)
    void saveHashes(@Param("hashes") List<String> hashes);
}