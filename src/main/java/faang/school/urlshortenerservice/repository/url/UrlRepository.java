package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """                        
            DELETE FROM url WHERE hash IN (SELECT hash FROM url WHERE created_at < NOW() - :years * INTERVAL '1 year') RETURNING hash
            """)
    List<String> getHashBatchAndDelete(int years);

    default Url getByHashOrThrow(String hash) {
        return findById(hash)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Url by hash %s not found", hash))
                );
    }
}