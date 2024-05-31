package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, ?)
            """)
    Set<Long> getUniqueNumbers(int max);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash IN(
            SELECT hash FROM hash ORDER BY hash ASC LIMIT :amount
            ) RETURNING *
            """)
    Set<Hash> getHashBatch(long amount);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO hash (hash) VALUES (:hashes)
            """)
    void saveHashes(Set<String> hashes);
}
