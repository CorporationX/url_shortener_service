package faang.school.urlshortenerservice.repository;


import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.repository.CrudRepository;

public interface UrlCacheRepository extends CrudRepository<Url, String> {
}
