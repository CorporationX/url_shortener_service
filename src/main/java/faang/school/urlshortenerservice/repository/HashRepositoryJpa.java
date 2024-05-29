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
            SELECT NEXTVAL('unique_numbers_seq') AS number FROM generate_series(1, :amount)
            """)
    List<Long> getUniqueNumbers(int amount);

    @Query(nativeQuery = true, value = """
            DELETE FROM hashes WHERE hash in (SELECT hash from hashes ORDER BY RANDOM() LIMIT :batchSize)
            RETURNING hash
            """)
    @Modifying
    @Transactional
    List<String> getHashBatch(int batchSize);
//    List<Hash> getHashBatch(int batchSize);

//    @Transactional
//    @Modifying
//    List<Hash> saveAllByMyFieldIn(List<String> hashes);

//    @Modifying
//    @Transactional
//    @Query(value = "INSERT INTO hashes (hash) VALUES (:hash)", nativeQuery = true)
//    void saveAllByMyFieldIn(List<String> hashes);

}
