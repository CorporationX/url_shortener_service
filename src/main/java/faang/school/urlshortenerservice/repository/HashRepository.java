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

    @Query(value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :amount)
            """,
            nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("amount") int amount);

    @Modifying
    @Query(value = """
            INSERT INTO hashes (hash)
            SELECT unnest(:hashes)
            """,
            nativeQuery = true)
    void save(@Param("hashes") List<String> hashes);

    @Modifying
    @Query(value = """
            WITH deleted_hashes AS (
                DELETE FROM hashes
                WHERE hash IN (
                    SELECT hash FROM hashes
                    LIMIT :amount
                )
                RETURNING hash
            )
            SELECT * FROM deleted_hashes
            """,
            nativeQuery = true)
    List<String> getHashBatch(@Param("amount") int amount);
}
