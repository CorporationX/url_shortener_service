package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Url> {

    @Query(value = "SELECT nextval('unique_numbers_seq') FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") long n);

    @Query(value = "WITH deleted AS (SELECT * FROM hash ORDER BY RANDOM() LIMIT :size) " +
            "DELETE FROM hash WHERE id IN (SELECT id FROM deleted) RETURNING *",
            nativeQuery = true)
    List<Hash> getHashBatch(@Param("size") int size);

    default <S extends Hash> List<S> saveHashes(List<Hash> entities) {
        return saveAll(entities);
    }
}
