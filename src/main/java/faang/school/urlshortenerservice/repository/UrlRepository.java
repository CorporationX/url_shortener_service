package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Evgenii Malkov
 */
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM url u where u.created_at < :expirationDate RETURNING u.hash")
    List<String> getExpiredHashesAndDelete(LocalDate expirationDate);
}
