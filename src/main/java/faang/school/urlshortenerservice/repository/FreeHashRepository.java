package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeHashRepository extends JpaRepository<Hash, String> {
    @Query(value = """
            SELECT h.hash
            FROM hashes h
            WHERE NOT EXISTS (
                SELECT 1 FROM urls u WHERE u.hash = h.hash
            )
            FOR UPDATE SKIP LOCKED
            LIMIT :count
            """, nativeQuery = true)
    List<String> fetchFreeHashes(@Param("count") int count);

    default String fetchFreeHash() {
        List<String> list = fetchFreeHashes(1);
        if (list.isEmpty()) {
            throw new IllegalStateException("No free hashes available");
        }
        return list.get(0);
    }
}