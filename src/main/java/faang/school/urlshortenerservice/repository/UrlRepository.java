package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """    
            SELECT nextval('unique_number_sequence') FROM generate_series(1,:maxRange)
    """)
    List<Long> getNextRange(int maxRange);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE id IN(
                SELECT id FROM hash ORDER BY id ASC LIMIT :amount 
            ) RETURNING *    
            """)
    List<Hash> findAndDelete(long amount);

    Optional<Url> findByHash(String hash);
}
