package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashJpaRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT nextval(:sequenceName) FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueValues(String sequenceName, int n);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM hash " +
            "   WHERE hash IN " +
            "   (SELECT hash FROM hash LIMIT :batchSize)" +
            "   RETURNING hash",
            nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int batchSize);
}
