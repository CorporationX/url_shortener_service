package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    @Resource(name = "generateExecutorService")
    private final ExecutorService generateExecutorService;

    public List<String> getHashes(int count) {
        log.info("Getting {} hashes from DB", count);
        List<String> hashes = hashRepository.getHashBatch(count);
        generateExecutorService.submit(hashGenerator::checkHashCountsAsync);
        log.info("Get {} hashes from DB", count);
        return hashes;
    }

    @Transactional
    public long saveFreeHashes(List<String> hashList) {
        log.info("Adding hashes count: {}", hashList.size());
        return hashRepository.saveAll(hashList);
    }
}
