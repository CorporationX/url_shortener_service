package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_sequences') FROM generate_series(1, :range)
            """)
    List<Long> getUniqueNumbers(int range);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE id IN (SELECT id FROM hash ORDER BY id ASC LIMIT :amount) RETURNING *
                    """)
    List<Hash> getHashBatch(long amount);
}
