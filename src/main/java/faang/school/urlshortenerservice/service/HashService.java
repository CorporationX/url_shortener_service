package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;

    public List<Long> getUniqueNumbers(int range) {
        log.debug("Getting unique numbers from the database");
        return hashRepository.getUniqueNumbers(range);
    }

    @Transactional
    public List<Hash> saveHashes(List<Hash> hashes) {
        log.info("Saving {} hashes to the database", hashes.size());
        return hashRepository.saveAll(hashes);
    }

    public List<String> getHashBatch(int amount) {
        log.debug("Getting hash batch from the database");
        return hashRepository.getHashBatch(amount);
    }

    public long getHashCount() {
        log.debug("Getting hash count from the database");
        return hashRepository.count();
    }

}
