package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<HashEntity, Long> {

    @Query(
            value = """
                    SELECT h.id
                    FROM hashes h
                    WHERE h.used = FALSE
                    ORDER BY h.id
                    LIMIT :count
                    """,
            nativeQuery = true
    )
    List<Long> getFreeIds(@Param("count") int count);

    @Query(value = """
            SELECT h.id
            FROM hashes h
            WHERE h.used = FALSE
            ORDER BY h.id
            LIMIT 1
            """, nativeQuery = true)
    Long findNextUnusedId();

    @Modifying
    @Query("UPDATE HashEntity h SET h.used = TRUE WHERE h.id = :id")
    void markUsed(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(
            value = """
                    UPDATE hashes h
                    SET h.used = FALSE
                    WHERE h.hash IN :hashes
                    """,
            nativeQuery = true
    )
    void returnHashes(@Param("hashes") List<String> hashes);
}