package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, Long> {

    @Modifying
    @Query("SELECT u.hash FROM Url u WHERE u.createdAt < :createdAt")
    List<String> findAndDeleteByCreatedAtBefore(LocalDateTime createdAt);
}
