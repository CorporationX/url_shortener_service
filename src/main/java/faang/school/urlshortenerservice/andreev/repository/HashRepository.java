package faang.school.urlshortenerservice.andreev.repository;

import faang.school.urlshortenerservice.andreev.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :count)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int count);

    default void save(List<String> hashes) {
        List<Hash> entities = hashes
                .stream()
                .map(Hash::new)
                .toList();
        saveAll(entities);
        flush();
    }

    @Query(value = """
           DELETE FROM hash WHERE hash IN(
           SELECT hash FROM hash ORDER BY random() LIMIT :count
           ) RETURNING hash
           """, nativeQuery = true)
    List<String> getHashBatch(@Param("count") int count);
}
