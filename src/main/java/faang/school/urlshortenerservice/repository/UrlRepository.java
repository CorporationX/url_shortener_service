package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE createdAt < NOW() - INTERVAL '1 year' RETURNING hash
            """)
    List<String> deleteOlderThanYear();
}


//    @Transactional
//    public List<String> deleteOldUrlsAndGetHashes(EntityManager entityManager) {
//        // Сначала получаем хэши
//        List<String> hashes = entityManager.createQuery(
//                        "SELECT u.hash FROM Url u WHERE u.createdAt <= :cutoffDate", String.class)
//                .setParameter("cutoffDate", LocalDateTime.now().minusYears(1))
//                .getResultList();
//
//        // Теперь удаляем старые URL
//        if (!hashes.isEmpty()) {
//            entityManager.createQuery(
//                            "DELETE FROM Url u WHERE u.createdAt <= :cutoffDate")
//                    .setParameter("cutoffDate", LocalDateTime.now().minusYears(1))
//                    .executeUpdate();
//        }
//
//        return hashes;
//    }

//}
