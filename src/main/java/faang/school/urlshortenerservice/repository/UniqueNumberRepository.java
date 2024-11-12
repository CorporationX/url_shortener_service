package faang.school.urlshortenerservice.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueNumberRepository {

    @Query(value = "SELECT nextval('unique_number_seq')", nativeQuery = true)
    Long getNextUniqueNumber();

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :count)", nativeQuery = true)
    List<Long> getNextUniqueNumbers(@Param("count") int count);
}
