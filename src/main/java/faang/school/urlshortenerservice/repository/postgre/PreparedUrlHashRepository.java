package faang.school.urlshortenerservice.repository.postgre;

import faang.school.urlshortenerservice.entity.PreparedUrlHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PreparedUrlHashRepository extends JpaRepository<PreparedUrlHash, String> {

    @Query(value = """
                SELECT p.hash
                FROM prepared_hashes p
                WHERE p.taken = false
                LIMIT :limit
            """, nativeQuery = true)
    Set<String> findFreeHashes(int limit);

    @Modifying
    @Query(value = """
                UPDATE prepared_hashes
                SET taken = true
                WHERE hash IN (:hashes)
            """, nativeQuery = true)
    void markHashesAsTaken(Set<String> hashes);

    @Modifying
    @Query(value = """
                UPDATE prepared_hashes
                SET taken = false
                WHERE hash = :hash
            """, nativeQuery = true)
    void markHashAsReusable(String hash);

    long count();
}
