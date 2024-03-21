package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.AssociationHashUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<AssociationHashUrl, String> {

    Optional<AssociationHashUrl> findByHash(String hash);

    @Query(nativeQuery = true, value = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash")
    List<String> deleteAndReturnOldUrls();

    @Query(nativeQuery = true, value = "SELECT COUNT(*) > 0 FROM url WHERE created_at < NOW() - INTERVAL '1 year'")
    boolean existsRecordsOlderThanOneYear();
}
