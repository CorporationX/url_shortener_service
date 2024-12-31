package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = """
    SELECT nextval('unique_number_seq') FROM generate_series(1, :count)
    """, nativeQuery = true)
    List<Long> getUniqueNumbers(int count);

    @Query("""
    SELECT h FROM Hash h
    ORDER BY RANDOM()
    LIMIT :batchSize""")
    List<Hash> getRandomHashBatch(int batchSize);
}