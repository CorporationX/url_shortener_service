package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.enity.FreeHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<FreeHash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :count)
            """)
    List<Long> getSequences(@Param(value = "count") int count);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM free_hash
            WHERE hash IN (
                SELECT hash FROM free_hash
                LIMIT :range
                FOR UPDATE SKIP LOCKED
            )
            RETURNING hash;
            """)
    List<String> findAndDelete(@Param("range") int range);
}
