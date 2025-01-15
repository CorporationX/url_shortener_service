package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, String> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM url WHERE created_at < :date RETURNING hash", nativeQuery = true)
    List<String> deleteOldUrlsAndGetHashes(@Param("date") LocalDate date);
}
