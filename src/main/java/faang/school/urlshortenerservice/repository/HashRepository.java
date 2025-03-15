package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(value = """
            SELECT nextval('hash_id_seq')
            FROM generate_series(1, ?1);
            """,
            nativeQuery = true)
    List<Long> getUniqueNumbers(int n);

    @Query(nativeQuery = true, value = """
                    SELECT *
                    FROM hash h
                    LIMIT ?1 OFFSET ?2
            """)
    List<Hash> findAllLimit(Integer start, Integer limit);
}
