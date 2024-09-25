package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface HashRepository extends JpaRepository<Hash, String>, CustomHashRepository {

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE id IN (
                SELECT id FROM hash ORDER BY id ASC LIMIT :amount
            ) RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}
