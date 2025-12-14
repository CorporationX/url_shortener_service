package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HashJpaRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM hash WHERE hash IN " +
            "(SELECT hash FROM hash ORDER BY RANDOM() LIMIT :limit) " +
            "RETURNING hash", nativeQuery = true)
    List<String> getAndDeleteRandomHashes(@Param("limit") int limit);
}