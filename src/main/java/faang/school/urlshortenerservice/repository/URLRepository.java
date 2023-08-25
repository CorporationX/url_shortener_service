package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.URLEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface URLRepository extends JpaRepository<URLEntity, Long> {
}
