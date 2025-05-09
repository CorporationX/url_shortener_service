package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CounterRepository extends JpaRepository<Counter, Long> {

    @Query(value = "SELECT * FROM global_counter FOR UPDATE", nativeQuery = true)
    Counter getValueForUpdate();
}
