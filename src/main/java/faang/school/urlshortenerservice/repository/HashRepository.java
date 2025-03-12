package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<String, Long> {

    @Query(value = "SELECT generate_series(nextval('unique_number_seq'), nextval('unique_number_seq') + :count - 1)",
            nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int count);
}
