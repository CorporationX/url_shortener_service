package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM pg_catalog.generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(@Param("maxRange") int maxRange);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash 
            WHERE hash IN (
                SELECT hash FROM hash
                ORDER BY hash ASC
                LIMIT :amount
            ) RETURNING *
            """)
    List<Hash> getHashBatch(@Param("amount") long amount);

    default void save(List<String> hashes) {
        List<Hash> hashEntities = hashes.stream()
                .map(Hash::new)
                .toList();
        this.saveAll(hashEntities);
    }
}
