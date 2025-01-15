package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Query("SELECT u.hash FROM Url u WHERE u.createdAt < :oneYearAgo")
    List<String> deleteUrlsOlderThan(LocalDate oneYearAgo);
}
