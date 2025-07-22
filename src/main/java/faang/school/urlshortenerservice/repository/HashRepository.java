package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            "SELECT nextval('unique_number_seq') AS generated_value" +
                        "FROM generated_series(1, 1000)"
            """)
    List<Long> getUniqueNumbers();

    @Query(nativeQuery = true, value = """
            "DELETE FROM hash 
            WHERE hash IN (
                SELECT hash 
                FROM hash
                ORDER BI RANDOM()
                LIMIT :limit
            )
            RETURNING *;"
            """)
    List<Hash> getHashBatch(int limit);
}
