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

    @Query("SELECT COUNT(h) FROM Hash h")
    long countHashes();

    @Query(value = "SELECT h FROM Hash h ORDER BY h.id ASC LIMIT :limit")
    List<Hash> findFirstHashes(@Param("limit") int limit);

    @Modifying
    @Query(value = "DELETE FROM Hash h WHERE h.id IN :ids")
    void deleteHashesByIds(@Param("ids") List<Long> ids);
}
