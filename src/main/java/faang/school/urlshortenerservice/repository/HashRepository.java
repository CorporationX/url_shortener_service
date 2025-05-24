package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash,Long> {

    @Query(nativeQuery = true, value = """
    SELECT nextval('unique_hash_number_seq') AS generated_value
     FROM generate_series(1, :range)
    """)
    List<Long> getUniqueHashNumbers(@Param("range") long range);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM free_hash WHERE id IN (
            SELECT id FROM free_hash ORDER BY id ASC LIMIT :amount
            ) RETURNING *
            """)
    List<Hash> findAndDelete(@Param("amount") long amount);
}