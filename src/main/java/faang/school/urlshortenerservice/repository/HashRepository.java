package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface HashRepository extends CrudRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :count)
            """)
    List<Long> getUniqueNumbers(int count);

    default void saveHashes(List<String> hashes) {
        List<Hash> entities = hashes.stream()
                .map(Hash::new)
                .collect(Collectors.toList());
        saveAll(entities);
    }


    @Query(nativeQuery = true, value = """
            WITH deleted AS (
                DELETE FROM hash 
                WHERE ctid IN (
                    SELECT ctid FROM hash 
                    LIMIT :size 
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING hash
            )
            SELECT * FROM deleted
            """)
    List<String> getHashBatch(@Param("size") int size);

}
