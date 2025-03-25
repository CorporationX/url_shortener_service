package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    Url findByUrl(String url);

    @Query(value = "DELETE FROM url WHERE expired_at < now() RETURNING *", nativeQuery = true)
    List<Url> deleteExpiredUrls();

}
