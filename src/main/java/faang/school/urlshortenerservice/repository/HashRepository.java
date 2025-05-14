package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :count)
            """)
    List<Long> getUniqueNumbers(int count);

    @Modifying
    @Query("""
            INSERT INTO hash(hash) VALUES (:hash)
            """)
    void saveHashes(List<String> hashes);


    @Modifying
    @Query("""
            DELETE FROM hash WHERE hash IN
            (SELECT hash  FROM hash LIMIT :size FOR UPDATE SKIP LOCKED)
            RETURNING hash
            """)
    List<String> getHashBatch(int size);

}
