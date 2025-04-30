package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import feign.Param;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("maxRange") int maxRange);


    @Modifying
    @Query(value = """
            DELETE FROM hash
            WHERE hash IN (SELECT hash FROM hash LIMIT :batchSize)
            RETURNING *;
            """, nativeQuery = true)
    List<Hash> getAndDelete(@Param("batchSize") int batchSize);

    @Transactional
    default <S extends Hash> List<S> saveAllBatch(Iterable<S> entities, EntityManager entityManager, int batchSize) {
        List<S> result = new ArrayList<>();

        int i = 0;
        for (S entity : entities) {
            entityManager.persist(entity);
            result.add(entity);

            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
            i++;
        }
        return result;
    }
}
