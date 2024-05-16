package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> getByHash(String hash);

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM Url u WHERE u.created_at < :period RETURNING *")
    List<Url> deleteOldUrl(LocalDateTime period);

}