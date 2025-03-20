package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT nextval('unique_numbers_seq') FROM generate_series(1, :limit)",
            nativeQuery = true)
    List<Long> getUniqueNumbers(int limit);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM hash WHERE hash IN "
            + "(SELECT hash FROM hash ORDER BY random() LIMIT :limit) "
            + "RETURNING *",
            nativeQuery = true)
    List<Hash> getFreeHashesBatch(@Param("limit") int limit);

}
