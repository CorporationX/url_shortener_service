package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
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
public class HashSaveRepository {
    private final HashRepository hashRepository;
    @Value("${batchSize}")
    private int batchSize;

    @Transactional
    public List<Hash> save(List<Hash> hashList) {
        List<Hash> hashBatch = new ArrayList<>(hashList.size());
        for (int i = 0; i < hashList.size(); i += batchSize) {
            int size = i + batchSize;
            if (size > hashList.size()) {
                size = hashList.size();
            }
            List<Hash> sub = hashList.subList(i, size);
            hashRepository.saveAll(sub);
            hashBatch.addAll(sub);
        }
        log.info("Hashes saved successfully {}", hashList);
        return hashBatch;
    }
}
