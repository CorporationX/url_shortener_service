package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :number)
            """)
    List<Long> getUniqueNumbers(@Param("number") long number);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(h.hash)
            FROM hash h
            """)
    Long getHashesSize();

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash
                FROM hash
                ORDER BY random()
                LIMIT :number
                FOR UPDATE SKIP LOCKED
            )
            RETURNING hash
            """)
    List<String> findAllAndDeleteByPackSize(@Param("number") int number);
}
