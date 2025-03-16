package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query("SELECT u FROM Url u WHERE u.createdAt < :date")
    List<Url> findUrlsOlderThan(LocalDateTime date);

    void deleteAll(List<Url> urls);
}
