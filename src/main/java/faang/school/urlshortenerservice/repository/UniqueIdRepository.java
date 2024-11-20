package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueIdRepository  extends CrudRepository<Url, Long> {
}
