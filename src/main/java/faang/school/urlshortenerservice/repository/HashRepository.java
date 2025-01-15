package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            WITH range AS (
                SELECT nextval('shortener_schema.unique_number_seq') AS start_value
            )
            SELECT generate_series(
                range.start_value,
                setval('shortener_schema.unique_number_seq', range.start_value + :rangeSize - 1)
            ) AS unique_number
            FROM range;
            """)
    List<Long> getUniqueNumbers(@Param("rangeSize") int rangeSize);

    @Query(nativeQuery = true, value = """
            WITH deleted_hashes AS (
                DELETE FROM shortener_schema.hash
                WHERE hash IN (
                    SELECT hash
                    FROM shortener_schema.hash
                    LIMIT :batchSize
                )
                RETURNING hash
            )
            SELECT hash FROM deleted_hashes;
            """)
    List<String> getHashBatch(@Param("batchSize") int batchSize);

    default void save(List<String> hashes, int batchSize, JdbcTemplate jdbcTemplate) {
        String sql = "INSERT INTO shortener_schema.hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, batchSize, (ps, hash) -> ps.setString(1, hash));
    }
}
