package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Hash;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :n)
            """)
    List<Long> getUniqueNumbers(int n);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash IN (SELECT hash FROM hash LIMIT :batchSize)
            RETURNING *
            """)
    List<Hash> getHashBatch(long batchSize);
}
