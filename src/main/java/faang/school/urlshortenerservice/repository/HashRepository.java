package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public interface  HashRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT nextval('unique_number_seq') AS num FROM generate_series(1, :count) ORDER BY RANDOM()", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int count);

    void saveAll(List<String> hashes);

    @Query(value = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT :batchSize) RETURNING hash", nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int batchSize);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT h FROM Hash h WHERE h.hash IN (SELECT h.hash FROM Hash h ORDER BY RANDOM() LIMIT :batchSize)")
    List<Hash> getHashBatchWithLock(@Param("batchSize") int batchSize);
}
