package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :countNumbers)
            """)
    List<Long> getUniqueNumbers(@Param("countNumbers") long countNumbers);

    @Query(nativeQuery = true, value = """
            SELECT * FROM hash ORDER BY hash LIMIT :count
            """)
    List<Hash> getHashBatch(@Param("count") long count);
}
