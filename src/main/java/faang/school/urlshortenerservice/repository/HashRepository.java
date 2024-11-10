package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true,
            value = "SELECT nextval('unique_number_seq') AS num FROM generate_series(1,:n)")
    List<Long> getUniqueNumbers(long n);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash IN (SELECT hash from ORDER BY random() LIMIT :n
            RETURNING hash)
            """)
    List<String> getHashBatch(long n);

}
