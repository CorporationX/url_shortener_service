package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :count)", nativeQuery = true)
    List<Long> getUniqueNumbers(int count);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO hash (hash) VALUES (:hash)", nativeQuery = true)
    void saveBatch(String hash);

    default void saveAllHashes(List<String> hashes) {
        for (String hash : hashes) {
            saveBatch(hash);
        }
    }

    @Modifying
    @Query(value = "DELETE FROM Hash WHERE hash IN (SELECT hash FROM Hash LIMIT :limit)" +
            " RETURNING hash", nativeQuery = true)
    List<String> getHashBatch(int limit);
}
