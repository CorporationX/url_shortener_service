package faang.school.urlshortenerservice.repository.uniquenumber;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UniqueNumberRepositoryImpl implements UniqueNumberRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long getLastUniqueNumber() {
        Query query = entityManager.createNativeQuery("SELECT last_value FROM unique_number_seq");
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public void setLastUniqueNumber(long finalNumber) {
        Query query = entityManager.createNativeQuery("SELECT setval('unique_number_seq', :finalNumber, false)");
        query.setParameter("finalNumber", finalNumber);
        query.getSingleResult();
    }
}
