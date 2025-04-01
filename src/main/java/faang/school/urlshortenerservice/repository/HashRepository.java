package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {
    Optional<Hash> findByHash(String hash);

    @Query(value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :numbers)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("numbers") int numbers);

    @Query(value = """
            INSERT INTO hash (id, hash)
            SELECT id, hash
            FROM url
            WHERE created_at < :createdAt
               OR expires_at < :now
            RETURNING id;
            """, nativeQuery = true)
    List<Long> insertToHashDeletedUrls(@Param("createdAt") LocalDate createdAt, @Param("now") LocalDate now);

    @Query(value = """
            SELECT h.*
            FROM hash h
            WHERE NOT EXISTS (
                    SELECT 1
                    FROM url u
                    WHERE h.hash = u.hash
            )
            LIMIT 1;
            """, nativeQuery = true)
    Optional<Hash> getHashNotExistInUrl();
}