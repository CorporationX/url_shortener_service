package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.hash.HashFree;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashFreeRepository extends CrudRepository<HashFree, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM free_hash WHERE id IN (SELECT id FROM free_hash ORDER BY random()" +
            " LIMIT :batchSize) RETURNING *")
    List<HashFree> getRandomHashFree(int batchSize);

    @Query("SELECT CASE WHEN COUNT(e) >= :getBatchSize THEN true ELSE false END FROM HashFree e")
    boolean ifCountMinElements(int getBatchSize);
}
