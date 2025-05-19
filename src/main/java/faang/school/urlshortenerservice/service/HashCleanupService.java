package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCleanupService {

    private final HashRepository hashRepository;

    @Scheduled(fixedDelayString = "${hash.cleanup.job.delay:3600000}")
    @Transactional
    public void cleanupUnusedHashes() {
        List<String> unusedHashes = hashRepository.findUnusedHashes();
        if (!unusedHashes.isEmpty()) {
            hashRepository.deleteAllById(unusedHashes);
            log.info("Deleted {} unused hashes", unusedHashes.size());
        } else {
            log.info("No unused hashes to delete");
        }
    }
}
