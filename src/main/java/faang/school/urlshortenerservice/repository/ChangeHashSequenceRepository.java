package faang.school.urlshortenerservice.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ChangeHashSequenceRepository {
    private final EntityManager entityManager;

    @Transactional
    public void setSequenceMaxValue(long value) {
        String sql = "ALTER SEQUENCE unique_hash_number_seq MAXVALUE %s".formatted(value);
        entityManager.createNativeQuery(sql)
                .executeUpdate();
    }

    @Transactional
    public void setSequenceMinValue(long value) {
        String sql = "ALTER SEQUENCE unique_hash_number_seq START %s MINVALUE %s RESTART %s"
                .formatted(value, value, value);
        entityManager.createNativeQuery(sql)
                .executeUpdate();
    }
}
