package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query("SELECT u.hash FROM Url u WHERE u.createdAt < :oneYearAgo")
    List<String> findHashesOfUrlsOlderThan(LocalDateTime oneYearAgo);

    @Modifying
    @Query("DELETE FROM Url u WHERE u.createdAt < :oneYearAgo")
    void deleteUrlsOlderThan(LocalDateTime oneYearAgo);
}