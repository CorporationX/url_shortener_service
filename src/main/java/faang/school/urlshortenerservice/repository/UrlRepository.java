package faang.school.urlshortenerservice.repository;


import faang.school.urlshortenerservice.model.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    @Modifying
    @Query(value = "DELETE FROM url WHERE created_at < :date RETURNING hash", nativeQuery = true)
    List<String> deleteByCreatedAtBefore(LocalDate date);
}