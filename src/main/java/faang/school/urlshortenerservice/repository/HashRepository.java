package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') AS generated_value
            FROM generate_series(1, :range);
            """)
    List<Long> getUniqueNumbers(int range);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash h WHERE hash IN (
            SELECT hash FROM h LIMIT :batchSize
            ) RETURNING *
            """)
    List<Hash> getHashBatch(int batchSize);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO hashes (hash)
            VALUES (:hash)
            """)
    void save(String hash);

    @Transactional
    default void saveAll(List<String> hashes){
        hashes.forEach(this::save);
    }
}
