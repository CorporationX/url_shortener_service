package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.URL;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends CrudRepository<URL, Long> {
}
