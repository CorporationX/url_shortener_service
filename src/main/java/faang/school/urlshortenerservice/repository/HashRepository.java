package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') from generate_series(1, :batchSize)
            """)
    List<Long> getUniqueNumbers(int batchSize);

    @Query(nativeQuery = true, value = """
            WITH selected_hashes AS (
            	SELECT hash
            	FROM hash
            	ORDER BY RANDOM()
            	LIMIT :size
            	FOR UPDATE
            	SKIP LOCKED
            )
            DELETE FROM hash USING selected_hashes
            WHERE hash.hash = selected_hashes.hash
            RETURNING hash.hash
            """)
    List<Hash> findAndDeleteBySize(int size);
}