package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_sequences') FROM generate_series(1, :count)
            """)
    List<Long> getUniqueNumbers(long count);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE id IN (SELECT id FROM hash ORDER BY id ASC LIMIT :count) RETURNING *
                    """)
    List<Hash> getHashBatch(long count);
}
