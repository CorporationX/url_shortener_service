package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepositoryJpa extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_numbers_seq') AS number FROM generate_series(1, :amount)
            """)
    List<Long> getUniqueNumbers(int amount);

    @Query(nativeQuery = true, value = """
            DELETE FROM hashes WHERE hash in (SELECT hash from hashes ORDER BY LIMIT :batchSize)
            RETURING hash
            """)
    @Modifying
    @Transactional
    List<Hash> getHashBatch(int batchSize);
}
