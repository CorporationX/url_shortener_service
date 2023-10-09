package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HashRepository extends CrudRepository<HashEntity, Long> {

    @Query(value = "DELETE FROM hash LIMIT ?1 RETURNING hash", nativeQuery = true)
    List<String> findAndDelete(int limit);
}
