package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexander Bulgakov
 */

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {
}
