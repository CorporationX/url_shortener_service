package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(value = "SELECT nextval('unique_number_seq') " +
            "FROM generate_series(1, :batch)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("batch") long batchSize);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM hash " +
            "WHERE hash IN (SELECT hash FROM hash LIMIT :n) " +
            "RETURNING hash", nativeQuery = true)
    List<String> getAndDeleteHashes(@Param("n")int quantity);
}