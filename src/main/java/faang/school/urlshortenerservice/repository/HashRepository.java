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
            FROM GENERATE_SERIES(1, :number)
            """)
    List<Long> getUniqueNumbers(long number);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash
                FROM hash
                ORDER BY random()
                LIMIT :number
                FOR UPDATE SKIP LOCKED
            )
            RETURNING hash;
            """)
    List<Hash> getHashBatch(long number);

    @Query(nativeQuery = true, value = """
            SELECT * FROM hash h
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """)
    Optional<Hash> getAvailableHash();

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash = :hash
            """)
    void markHashAsReserved(String hash);
}
