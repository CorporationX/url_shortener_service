package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Hash> {
    Optional<Url> findByHash(String hash);

    List<Url> findByCreatedAtBefore(LocalDateTime createdAt);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM urls WHERE hash IN (:hashes) RETURNING hash
            """)
    List<String> deleteByHashAndReturn(@Param("hashes") List<String> hashes);
}
