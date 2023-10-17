package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlRedis;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UrlCacheRepository extends CrudRepository<UrlRedis, String> {
    Optional<UrlRedis> findById(@NonNull String hash);
}
