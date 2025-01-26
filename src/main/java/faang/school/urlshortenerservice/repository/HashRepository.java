package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(int maxRange);

    @Query(nativeQuery = true, value = """
        DELETE FROM hash WHERE hash IN (SELECT hash FROM hash ORDER BY hash ASC LIMIT :amount)
        RETURNING *
    """)
    List<Hash> getHashes(long amount);

    @Query(nativeQuery = true, value = """
    INSERT INTO hash (hash)
    VALUES (hash.hash)
    ON CONFLICT (hash) DO NOTHING 
    """)
    void insertHashes(List<String> hashes);
}

//@org.springframework.data.repository.query.Param("maxRange")