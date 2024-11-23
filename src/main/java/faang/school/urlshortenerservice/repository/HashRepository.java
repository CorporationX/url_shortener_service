package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Modifying
    @Query(value = """
            DELETE FROM hash 
            WHERE hash IN (SELECT h.hash FROM hash h ORDER BY h.created_at LIMIT :limit)
            RETURNING hash.hash""",
            nativeQuery = true)
    List<String> findTopNHashes(int limit);

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)", nativeQuery = true)
    List<Long> getUniqueNumbers(int count);
}
