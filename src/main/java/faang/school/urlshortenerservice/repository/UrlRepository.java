package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(value = "DELETE FROM url WHERE DATE_PART('year', created_at) < DATE_PART('year', CURRENT_DATE) RETURNING *", nativeQuery = true)
    @Modifying
    List<Url> deleteOldShortUrls();
}
