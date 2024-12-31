package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    Optional<Url> findByHashValue(String hashValue);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Url> deleteByValidatedAtBefore(LocalDateTime date);
}