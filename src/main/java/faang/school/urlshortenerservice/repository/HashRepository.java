package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(
            value = "SELECT nextval('unique_number_seq') FROM generate_series (1, :amount)",
            nativeQuery = true
    )
    List<Long> getUniqueNumbers(@Param("amount") int amount);

    @Query(
            value = "DELETE FROM hash WHERE hash IN " +
                    "(SELECT hash from hash LIMIT :amount) " +
                    "RETURNING hash",
            nativeQuery = true
    )
    List<String> getHashBatch(@Param("amount") int amount);
}
