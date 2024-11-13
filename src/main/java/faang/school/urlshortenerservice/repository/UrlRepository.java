package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, String> {


}
