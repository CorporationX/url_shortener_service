package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {

    @Modifying
    @Query(value = "DELETE FROM hash WHERE ctid IN (SELECT ctid FROM hash LIMIT :batchSize) RETURNING hash",
            nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int batchSize);

    @Query(value = "SELECT COUNT(*) >= :limit FROM hash", nativeQuery = true)
    boolean existsHashesAtLeast(@Param("limit") int limit);
}
