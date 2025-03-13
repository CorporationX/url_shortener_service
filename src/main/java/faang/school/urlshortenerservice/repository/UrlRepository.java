package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Url findByHash(String hash);

    Page<Url> findByCreatedAtBefore(LocalDateTime expirationDate, Pageable pageable);
}
