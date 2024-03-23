package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM url u WHERE u.created_at < NOW() - INTERNAL '1 year' RETURNING u.hash")
    List<String> clearOldUrls();

}
