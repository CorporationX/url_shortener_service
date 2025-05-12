package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {

    @Modifying
    @Query(value = "DELETE FROM Hash WHERE hash IN " +
            "(SELECT h.hash FROM Hash h ORDER BY RANDOM() LIMIT :batchSize) RETURNING hash", nativeQuery = true)
    List<String> getHashBatch(int batchSize);
}
