package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT EXISTS(SELECT 1 FROM hash WHERE base64_hash = :base64Hash)
            """)
    boolean existsByBase64Hash(String base64Hash);

    @Query(nativeQuery = true,value = """
            DELETE FROM hash WHERE id IN(
            SELECT id FROM hash ORDER BY id ASC LIMIT :amount
            ) RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}
