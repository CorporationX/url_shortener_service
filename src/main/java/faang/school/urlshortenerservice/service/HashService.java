package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashGeneratorExecutor;

    public List<String> getHashes(int count) {
        List<String> hashes = hashRepository.getHashes(count);
        if (hashes.size() < count) {
            hashGeneratorExecutor.submit(hashGenerator::checkAndGenerateHashesAsync);
        }
        log.debug("Retrieved {} hashes from repository", hashes.size());
        return hashes;
    }

    @Transactional
    public void saveFreeHashes(List<String> hashes) {
        if (hashes.isEmpty()) {
            log.debug("No hashes to save");
            return;
        }
        hashRepository.saveHashes(hashes);
        log.info("Saved {} free hashes to repository", hashes.size());
    }
}
