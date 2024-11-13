package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query("SELECT h FROM hash h ORDER BY h.generatedAt DESC")
    List<Hash> findTopNHashes(int limit);

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int count);

    @Query(value = """ 
            WITH deleted AS (DELETE FROM hash WHERE id IN
            (SELECT id FROM hash ORDER BY RANDOM() LIMIT :limit) RETURNING *)
            SELECT * FROM deleted
            """, nativeQuery = true)
    List<Hash> getHashBatch(@Param("limit") int limit);

    List<Hash> save(List<Hash> hashes);
}
