package faang.school.urlshortenerservice.repository.jpa;

import faang.school.urlshortenerservice.model.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
                    SELECT nextval('url_hash_seq') FROM generate_series(1, :maxCount);
            """)
    List<Long> getNextSequenceValues(int maxCount);

    @Query(nativeQuery = true, value = """
                    DELETE FROM hash
                    WHERE id IN
                        (SELECT id
                        FROM hash
                        LIMIT :batchSize
                        FOR UPDATE SKIP LOCKED)
                    RETURNING *;
            """)
    @Modifying
    List<Hash> getHashesBatch(int batchSize);
}
