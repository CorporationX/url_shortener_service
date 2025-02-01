package faang.school.urlshortenerservice.repository.interfaces;

import faang.school.urlshortenerservice.model.Urls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlsJpaRepository extends JpaRepository<Urls, String> {
    Optional<Urls> findByHash(String hash);
}