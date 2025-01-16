package faang.school.urlshortenerservice.repository;


import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface HashRepository extends CrudRepository<Hash, String>, HashRepositoryCustom  {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :count)", nativeQuery = true)
    long getNextSequenceValue(int count);
}
