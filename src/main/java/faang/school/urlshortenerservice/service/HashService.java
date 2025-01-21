package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {
    private final HashRepository hashRepository;

    public List<Long> getUniqueSeqNumbers(Long amount) {
        List<Long> result = hashRepository.getUniqueSeqNumbers(amount);
        log.info("Received {} unique sequence numbers to generate hashes.", result.size());
        return result;
    }

    public List<String> getAndDeleteHashBatch(Long amount) {
        List<String> result = hashRepository.getAndDeleteHashBatch(amount);
        log.info("Received and deleted {} hashes from hash repository.", result.size());
        return result;
    }

    public void saveHashes(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
        log.info("Saved {} new hashes to hash repository.", hashes.size());
    }

    public Long getHashRepositorySize() {
        return hashRepository.count();
    }
}
