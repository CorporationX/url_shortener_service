package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = "SELECT NEXTVAL('unique_number_seq') FROM generate_series(1, ?1)")
    List<Long> getUniqueNumbers(long quantity);

    void save(List<String> hashes);

    @Query(nativeQuery = true, value = "WITH deleted_hashes AS ( " +
            "DELETE FROM hash " +
            "WHERE hash IN ( " +
            "SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?1 " +
            ") RETURNING hash " +
            ") SELECT hash FROM deleted_hashes;")
    List<Hash> getHashBatch(int batchSize);
}
