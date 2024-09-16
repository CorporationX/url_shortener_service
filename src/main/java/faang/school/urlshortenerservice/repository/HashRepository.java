package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, 10000)
            """)
    List<Long> getNextRange();

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash IN (
                SELECT hash FROM hash ORDER BY hash ASC LIMIT :n
            ) RETURNING *
            """)
    List<String> findAndDelete(@Param("n") long amount);

}
