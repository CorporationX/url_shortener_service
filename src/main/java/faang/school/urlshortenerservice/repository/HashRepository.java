package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${hash-service.count-get-hash}")
    public int countHash;
    private final JpaHashRepository jpaHashRepository;

    public List<Long> getUniqueNumbers(long countSequence) {
        return jpaHashRepository.getUniqueNumbers(countSequence);
    }

    public void save(List<Hash> hashes) {
        jpaHashRepository.saveAll(hashes);
    }

    public List<Hash> getBatches() {
        return jpaHashRepository.getHashBatches(countHash);
    }
}
