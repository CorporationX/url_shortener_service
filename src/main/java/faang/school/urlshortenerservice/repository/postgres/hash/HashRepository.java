package faang.school.urlshortenerservice.repository.postgres.hash;

import faang.school.urlshortenerservice.model.hash.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :n)
            """)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash 
                WHERE hash IN (SELECT hash FROM hash ORDER BY random() LIMIT :n) 
            RETURNING hash.hash;
            """)
    List<String> getBatchAndDelete(@Param("n") int n);
}
