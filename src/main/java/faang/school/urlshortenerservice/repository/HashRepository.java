package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = """
            SELECT nextval('unique_numbers_seq')FROM generate_series(1, :range)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("range") int range);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM hashes
            WHERE hash IN (
                            SELECT hash FROM hashes
                            ORDER BY random()
                            LIMIT :batchSize
                            FOR UPDATE SKIP LOCKED)
            RETURNING *
            """, nativeQuery = true)
    List<String> removeAndGetHashBatch(@Param("batchSize") int batchSize);
  }
