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

    public List<Long> getUniqueSeqNumbers(Long count) {
        List<Long> result = hashRepository.getUniqueSeqNumbers(count);
        log.info("Received {} unique sequence numbers to generate hashes.", result.size());
        return result;
    }

    public List<String> getAndDeleteHashBatch(Long count) {
        List<String> result = hashRepository.getAndDeleteHashBatch(count);
        log.info("Received and deleted {} hashes from hash repository.", result.size());
        return result;
    }

    public List<Hash> saveHashes(List<Hash> hashes) {
        List<Hash> result = hashRepository.saveAll(hashes);
        log.info("Saved {} new hashes to hash repository.", result.size());
        return result;
    }

    public Long getHashRepositorySize() {
        return hashRepository.count();
    }
}
