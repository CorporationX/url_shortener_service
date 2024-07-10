package faang.school.urlshortenerservice.repository.jpa;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :n);
            """)
    List<Long> getUniqueNumbers(long n);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO hashes (hash)
            VALUES (:hash)
            """)
    void save(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hashes
            WHERE hash IN (
                SELECT hash
                FROM hashes
                ORDER BY RANDOM()
                LIMIT :batchSize
            )
            RETURNING hash
            """)
    List<String> getHashBatch(long batchSize);

    @Transactional
    default void saveAll(List<String> hashes) {
        hashes.forEach(this::save);
    }
}
