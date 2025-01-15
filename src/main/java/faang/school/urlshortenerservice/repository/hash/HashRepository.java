package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)")
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO hash (hash) VALUES (:hash)")
    void saveBatch(@Param("hash") List<String> hashes);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "WITH deleted AS (" +
            " DELETE FROM hash " +
            " WHERE hash IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT :n) " +
            " RETURNING hash) " +
            " SELECT hash FROM deleted")
    List<String> getHashBatch(@Param("n") int n);
}
