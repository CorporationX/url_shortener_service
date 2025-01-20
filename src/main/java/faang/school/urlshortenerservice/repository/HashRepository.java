package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(
            value = """
                    SELECT nextval('unique_number_seq')
                    FROM generate_series(1, :count)
                    """,
            nativeQuery = true
    )
    List<Long> getUniqueNumbers(int count);

    @Transactional
    @Modifying
    @Query(
            value = """
                    WITH deleted AS (
                        DELETE FROM hash
                        WHERE hash IN (
                            SELECT hash
                            FROM hash
                            LIMIT :count
                        )
                        RETURNING hash
                    )
                    SELECT hash
                    FROM deleted
                    """,
            nativeQuery = true
    )
    List<String> getHashBatch(int count);
}
