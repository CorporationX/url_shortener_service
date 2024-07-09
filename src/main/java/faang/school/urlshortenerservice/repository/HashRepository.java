package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${hash-repository.count-get-hash}")
    public int countHash;
    private final JpaHashRepository jpaHashRepository;

    public List<Long> getUniqueNumbers(long countSequence) {
        return jpaHashRepository.getUniqueNumbers(countSequence);
    }

    public void save(List<String> hashes) {
        List<Hash> entities = hashes.stream()
                .map(Hash::new)
                .collect(Collectors.toList());
        jpaHashRepository.saveAll(entities);
    }

    public List<Hash> getBatches() {
        return jpaHashRepository.getHashBatches(countHash);
    }
}
