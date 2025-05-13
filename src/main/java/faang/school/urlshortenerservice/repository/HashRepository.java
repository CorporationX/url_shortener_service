package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = """
            SELECT nextval("unique_number_sequence")
            FROM generate_series(1, :count)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int n);

    @Modifying
    @Transactional
    @Query(value = """
                 INSERT INTO hash (hash) VALUES (?#{#hashes.![hash]})
                 ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    void saveHashes(List<Hash> hashes);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM hash
            WHERE hash IN (
            SELECT hash FROM hash
            ORDER BY created_at
            LIMIT :batchSize
            )
            RETURNING hash
            """, nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int batchSize);
}
