package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT NEXTVAL('unique_number_seq') from generate_series(1, :queriesAmount)", nativeQuery = true)
    public List<Long> getUniqueNumbers(@Param("queriesAmount") int queriesAmount);
}