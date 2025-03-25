package faang.school.urlshortenerservice.service.hash.impl;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {
    private final HashRepository hashRepository;

    public List<Long> generateNewNumbers(Long n) {
        log.info("Generate New Numbers, n = {}", n);
        return hashRepository.getUniqueNumbers(n);
    }

    @Override
    public Long getHashesCount() {
        return hashRepository.count();
    }

    @Transactional
    public void saveHashesBatch(List<Hash> hashes) {
        log.info("SaveHashesBatch, size: {}", hashes.size());
        hashRepository.hashBatchSave(hashes);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<Hash> getBatchHashesAndDelete(int size) {
        List<Hash> hashes = hashRepository.getHashBatchAndDelete(size);

        log.info("GetBatchHashesAndDelete size: {}", hashes.size());
        return hashes;
    }
}
