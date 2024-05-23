package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(nativeQuery = true, value = "SELECT nextval('id_seq') FROM generate_series(1, ?1)")
    List<Long> getUniqueNumbers(int batchSize);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING *")
    List<Hash> getHashBatch(int batchSize);

    @Query(nativeQuery = true, value = "DELETE FROM hash LIMIT 1 RETURNING *;")
    Hash findFirst();
}
