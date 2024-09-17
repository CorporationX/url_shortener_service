package faang.school.urlshortenerservice.repository.url;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    @Value("${url.scheduler.expired-interval}")
    private String INTERVAL;

    @PersistenceContext
    private final EntityManager entityManager;

    public List<String> deleteExpiredUrlsReturningHashes() {
        String query = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '%s' RETURNING hash".formatted(INTERVAL);
        return entityManager.createNativeQuery(query).getResultList();
    }
}
