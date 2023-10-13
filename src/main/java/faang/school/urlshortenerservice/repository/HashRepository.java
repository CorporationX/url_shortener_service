package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, String> {
    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_sequence') + generate_series(1, :limit) - 1")
    List<Long> getUniqueNumbers(@Param("limit") int limit);
}