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
    @Query(nativeQuery = true, value = """
            DELETE FROM hash h1
            WHERE h1.hash IN (
                SELECT h2.hash FROM hash h2
                LIMIT :count
            )
            RETURNING h1.hash;
            """)
    List<String> getHashBatch(int count);
}
