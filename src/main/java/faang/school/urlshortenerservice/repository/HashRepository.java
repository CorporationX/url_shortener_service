package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?1)")
    List<Long> getUniqueNumbers(int n);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO hashes (hash) VALUES (:hash)")
    void saveSingleHash(@Param("hash") String hash);

    default void saveBatch(List<String> hashes) {
        for (String hash : hashes) {
            saveSingleHash(hash);
        }
    }

    @Query(nativeQuery = true, value = "SELECT hash FROM hashes ORDER BY random() LIMIT :n")
    List<String> selectRandomHashes(@Param("n") int n);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM hashes WHERE hash IN :hashes")
    void deleteHashes(@Param("hashes") List<String> hashes);
}
