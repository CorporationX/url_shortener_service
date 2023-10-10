package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashJpaRepository {
    private final HashRepository hashRepository;
    @Value("${batchSize}")
    private int batchSize;

    @Transactional
    public List<Hash> saveBatch(List<Hash> hashes) {
        List<Hash> result = new ArrayList<>(hashes.size());
        for (int i = 0; i < hashes.size(); i += batchSize) {
            int size = i + batchSize;
            if (size > hashes.size()) {
                size = hashes.size();
            }
            List<Hash> sub = hashes.subList(i, size);
            hashRepository.saveAll(sub);
            result.addAll(sub);
        }
        log.info("Hashes was successfully saved {}", hashes);
        return result;
    }
}
