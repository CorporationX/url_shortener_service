package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
        SELECT nextval('unique_hash_number_seq')
        FROM generate_series(1, :maxRange)
    """)
    List<Long> getUniqueNumbers(@Param("maxRange") int maxRange);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM hash
        WHERE hash IN (
            SELECT hash
            FROM hash
            LIMIT :amount
            )
        RETURNING *
    """)
    List<Hash> getAndDelete(@Param("amount") long amount);

    @Modifying
    @Query(value = """
        INSERT INTO hash (hash)
        SELECT unnest(CAST(:hashes AS text[]))
        ON CONFLICT (hash) DO NOTHING
    """, nativeQuery = true)
    void saveHashes(@Param("hashes") String[] hashes);

    @Query(nativeQuery = true, value = """
        SELECT COUNT(*) FROM hash
    """)
    int getHashesSize();
}
