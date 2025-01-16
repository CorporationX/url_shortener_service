package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
                    SELECT nextval('url_hash_seq') FROM generate_series(1, :maxCount);
            """)
    List<Long> getNextSequenceValues(int maxCount);

    @Query(nativeQuery = true, value = """
                    DELETE FROM hash
                    WHERE ctid IN
                        (SELECT ctid
                        FROM hash
                        LIMIT :batchSize FOR UPDATE SKIP LOCKED)
                    RETURNING *;
            """)
    List<Hash> getHashesBatch(int batchSize);
}
