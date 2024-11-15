package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            DELETE FROM hash h1
            WHERE h1.hash IN (
                SELECT h2.hash FROM hash h2
                LIMIT :count
            )
            RETURNING h1.hash;
            """)
    List<String> getHash(int count);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash h1
            WHERE h1.hash = (
                SELECT h2.hash FROM hash h2
                LIMIT 1
            )
            RETURNING h1.hash;
            """)
    Optional<String> getHash();
}
