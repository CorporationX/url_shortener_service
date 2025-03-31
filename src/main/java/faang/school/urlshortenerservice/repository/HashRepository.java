package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.event.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getNextRange(@Param("maxRange")int maxRange);

    @Modifying
    @Query(nativeQuery = true, value = """
    DELETE FROM hash
    WHERE hash IN (SELECT hash FROM hash LIMIT :amount)
    RETURNING hash
    """)
    List<Hash> findAndDelete(@Param("amount") long amount);
}
