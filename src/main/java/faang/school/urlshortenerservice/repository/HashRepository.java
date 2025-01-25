package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String>, HashRepositoryCustom{
    @Query(value = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT :batchSize) RETURNING hash", nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int batchSize);

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :amount)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("amount") long amount);
}
