package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.HashEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<HashEntity, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :count)
            """)
    List<Long> getUniqueNumbers(int count);

    @Query("""
        SELECT h FROM HashEntity h
        ORDER BY RANDOM()
        LIMIT :batchSize
        """)
    List<HashEntity> getRandomHashBatch(int batchSize);
}
