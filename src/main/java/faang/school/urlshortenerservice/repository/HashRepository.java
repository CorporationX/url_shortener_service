package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval ('unique_number_seq') FROM generate_series(1, :uniqueNumbers)
            """)
    List<Long> getUniqueNumbers(@Param("uniqueNumbers") int uniqueNumbers);

    @Modifying
    @Query(nativeQuery = true, value = """
        WITH deleted_hashes AS (
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash FROM hash
                ORDER BY hash
                LIMIT :countHash
            )
            RETURNING hash
        )
        SELECT hash FROM deleted_hashes
        """)
    List<String> getHashBatch(@Param("countHash") int countHash);

}
