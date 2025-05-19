package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<HashEntity, String> {
    List<HashEntity> findAllBy(Pageable pageable);

    @Query("SELECT h.hash FROM HashEntity h WHERE h.hash NOT IN (SELECT u.hash FROM UrlEntity u)")
    List<String> findUnusedHashes();
}
