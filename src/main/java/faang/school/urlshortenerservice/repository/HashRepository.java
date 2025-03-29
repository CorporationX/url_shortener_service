package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(
            nativeQuery = true,
            value = "SELECT nextval('unique_id_seq') " +
                    "AS next_value FROM generate_series(1, :toUniqueNumber)"
    )
    List<Long> getUniqueNumbers(@Param("toUniqueNumber") int toUniqueNumber);

    @Query(
            nativeQuery = true,
            value = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT :count) RETURNING hash"
    )
    List<Hash> getHashBatch(@Param("count") int count);
}
