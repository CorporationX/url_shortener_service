package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HashRepository extends JpaRepository<HashEntity, String> {
    List<HashEntity> findAllBy(Pageable pageable);
}
