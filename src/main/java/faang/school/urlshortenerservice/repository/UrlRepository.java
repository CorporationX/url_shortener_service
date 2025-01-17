package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Hash> {

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE created_at < DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 YEAR)
            ) RETURNING *
            """)
    List<Hash> getAndDeleteAfterOneYear();

    Optional<Url> findByHash(String hash);

}
