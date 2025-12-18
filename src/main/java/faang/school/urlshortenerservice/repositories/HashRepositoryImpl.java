package faang.school.urlshortenerservice.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HashRepositoryImpl implements HashRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<String> findAndDelete(long amount) {
        Query query = entityManager.createNativeQuery("""
                DELETE FROM hash
                WHERE id IN (
                    SELECT id FROM hash
                    ORDER BY id ASC 
                    LIMIT :amount
                ) 
                RETURNING hash
                """);
        query.setParameter("amount", amount);
        return (List<String>) query.getResultList();
    }
}
