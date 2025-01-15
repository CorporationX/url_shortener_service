package faang.school.urlshortenerservice.repository.sequence;

import faang.school.urlshortenerservice.entity.sequence.UniqueNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueNumberSequenceRepository extends JpaRepository<UniqueNumberSequence, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :count)
            """)
    List<Long> getUniqueNumbers(@Param("count") int count);
}
