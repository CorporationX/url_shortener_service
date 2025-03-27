package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT NEXTVAL('unique_number_seq') " +
            "FROM generate_series(1, :number)", nativeQuery = true)
    List<Long> getUniqueNumbers(int number);

    // lock on bd
    @Query(value = """
        DELETE FROM hashes 
        WHERE hash IN (
            SELECT hash FROM hashes
            ORDER BY random()
            LIMIT :batchSize
        )
        RETURNING *
    """, nativeQuery = true)
    List<String> getHashBatch(int batchSize);
}

