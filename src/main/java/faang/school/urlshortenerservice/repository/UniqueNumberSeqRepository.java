package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UniqueNumberSeq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UniqueNumberSeqRepository extends JpaRepository<UniqueNumberSeq, Long> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :count)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int count);
}
