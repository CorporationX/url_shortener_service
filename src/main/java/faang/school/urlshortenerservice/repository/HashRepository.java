package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT NEXTVAL('unique_number_seq')
            FROM GENERATE_SERIES(1, :n)
            """)
    List<Long> getUniqueNumbers(long n);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash NOT IN (
                SELECT hash FROM url
            )
            AND hash NOT IN (
                SELECT hash
                FROM hash
                ORDER BY random()
                LIMIT :n
                FOR UPDATE SKIP LOCKED
            )
            RETURNING hash;
            """)
    List<Hash> getHashBatch(int n);

    @Query(nativeQuery = true, value = """
             SELECT character_maximum_length
             FROM information_schema.columns
             WHERE table_name = 'hash' AND column_name = 'hash';
            """)
    int getCharLength();

    @Query(nativeQuery = true, value = """
            SELECT * FROM hash h
            WHERE NOT EXISTS (
                SELECT 1 FROM url u
                WHERE u.hash = h.hash
            )
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """)
    Optional<Hash> findUnusedHash();
}
