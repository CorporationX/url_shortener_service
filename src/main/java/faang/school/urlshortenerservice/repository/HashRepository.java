package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('url_shortener_schema.unique_number_seq')
            FROM generate_series(1, :countValue)
            """)
    List<Long> getUniqueNumbers(@Param("countValue") int count);

    @Query(nativeQuery = true, value = """
            DELETE FROM url_shortener_schema.hash
            WHERE hash IN (
                SELECT hash
                FROM url_shortener_schema.hash
                LIMIT :batchSize)
            RETURNING hash;
            """)
    List<String> getHashBatch(@Param("batchSize") int batch);

}
