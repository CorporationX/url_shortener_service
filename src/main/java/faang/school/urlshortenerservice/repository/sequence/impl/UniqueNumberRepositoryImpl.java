package faang.school.urlshortenerservice.repository.sequence.impl;

import faang.school.urlshortenerservice.config.sequence.NumberSequenceProperties;
import faang.school.urlshortenerservice.repository.sequence.UniqueNumberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UniqueNumberRepositoryImpl implements UniqueNumberRepository {

    private final NumberSequenceProperties numberSequenceProperties;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Long> getUniqueNumbers() {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, :range)";

        @SuppressWarnings("unchecked")
        List<Long> result = entityManager
                .createNativeQuery(sql)
                .setParameter("range", numberSequenceProperties.getRange())
                .getResultList();

        return result;
    }
}
