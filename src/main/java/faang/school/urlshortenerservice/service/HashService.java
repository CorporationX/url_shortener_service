package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.aop.CheckAndGenerateHash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;

    @CheckAndGenerateHash
    public List<String> getHashes(int count) {
        log.info("Getting {} hashes from DB", count);
        List<String> hashes = hashRepository.getHashBatch(count);
        log.info("Get {} hashes from DB", count);
        return hashes;
    }

    @Transactional
    public long saveFreeHashes(List<String> hashList) {
        log.info("Adding hashes count: {}", hashList.size());
        return hashRepository.saveAll(hashList).size();
    }
}
