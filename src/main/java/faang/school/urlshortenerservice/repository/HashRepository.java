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
            SELECT nextval('unique_number_seq') FROM generate_series(1, ?)
            """)
    Set<Long> getUniqueNumbers(long max);

    @Query(nativeQuery = true,value = """
            DELETE FROM hash WHERE id IN(
            SELECT id FROM hash ORDER BY id ASC LIMIT :amount
            ) RETURNING *
            """)
    Set<Hash> getHashBatch(long batchSize);

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, ?)
            """)
    Set<Long> getUniqueNumbers(int max);


    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO hash (base64_hash) VALUES (:hashes)
            """)
    void saveHashes(Set<String> hashes);
}
