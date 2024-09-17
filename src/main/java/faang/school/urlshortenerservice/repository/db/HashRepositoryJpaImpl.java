package faang.school.urlshortenerservice.repository.db;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Profile(value = "jpa_profile")
@Repository
@RequiredArgsConstructor
public class HashRepositoryJpaImpl implements HashRepository {

    private final JpaHashRepository jpaHashRepository;

    @Override
    public List<Long> getUniqueNumbers(long n) {
        return jpaHashRepository.getUniqueNumbers(n);
    }

    @Override
    public List<String> pollHashBatch(long n) {
        return jpaHashRepository.pollHashBatch(n);
    }

    @Override
    public void saveBatch(List<String> hashes) {
        List<Hash> entities = hashes.stream().map(Hash::new).toList();
        jpaHashRepository.saveAll(entities);
    }

    @Override
    public int getHashesNumber() {
        return jpaHashRepository.getHashesNumber();
    }
}
