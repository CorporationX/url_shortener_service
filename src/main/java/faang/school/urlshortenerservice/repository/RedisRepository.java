package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RedisRepository extends JpaRepository<String, String> {
}
