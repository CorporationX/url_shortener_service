package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(value = "SELECT number FROM hash Where lock=0 ORDER BY id LIMIT :limit", nativeQuery = true)
    List<Long> findTopN(@Param("limit") int limit);

    Page<Hash> findAll(Pageable pageable);
}
