package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.hash.Hash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

}
