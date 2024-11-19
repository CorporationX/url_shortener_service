package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final HashProperties hashProperties;

    @Transactional()
    public List<Long> getUniqueNumbers(long n) {
        return hashRepository.getUniqueNumbers(n);
    }

    @Transactional()
    public List<Hash> getHashBatch() {
        return hashRepository.getHashBatch(hashProperties.getBatchSizeForGetHashes());
    }

    @Transactional()
    public void saveAllHashes(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }

    @Transactional(readOnly = true)
    public int getCharLength() {
        return hashRepository.getCharLength();
    }
}
