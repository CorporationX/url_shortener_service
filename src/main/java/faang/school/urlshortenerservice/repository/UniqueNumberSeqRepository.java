package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueNumberSeqRepository {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :count)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int count);
}
