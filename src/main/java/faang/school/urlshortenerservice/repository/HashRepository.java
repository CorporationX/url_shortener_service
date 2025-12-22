package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Modifying
    @Transactional
    @Query(value = """
            WITH delete_batch AS (
              SELECT h.ctid
              FROM hash h
              ORDER BY h.id
              FOR UPDATE SKIP LOCKED
              LIMIT :limit
            )
            DELETE FROM hash h
            USING delete_batch b
            WHERE h.ctid = b.ctid
            RETURNING h.*;
            """,
            nativeQuery = true)
    List<Hash> findAndDelete(int limit);

    @Query(value = "SELECT count(h) FROM Hash h")
    Long countUnusedHashes();
}
