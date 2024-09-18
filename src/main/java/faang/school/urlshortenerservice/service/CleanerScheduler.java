package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${cron_expression}")
    @Transactional
    public void deleteOldRecords() {
        List<String> oldStringHashes = urlRepository.deleteAllByCreatedAtBefore();
        List<Hash> oldHashes = oldStringHashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAllAndFlush(oldHashes);

    }
}
