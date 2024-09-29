package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortUrl(String shortUrl);

    @Modifying
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM url WHERE created_at < :outdated RETURNING *
                    """
    )
    List<Url> popAllOldUrl(LocalDate outdated);
}
