package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${spring.scheduling.period}")
    private String period;

    @Transactional
    @Scheduled(cron = "${spring.scheduling.cron}")
    public void clean() {
        List<Url> oldUrl = urlRepository.getOldHashesAndDelete(period);
        List<Hash> hashesToSave = oldUrl.stream()
                .map(Url::getHash)
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashesToSave);

        log.info("Total cleaned hashes that older than 1 year: {}", hashesToSave.size());
    }

    @Scheduled(cron = "${spring.scheduling.cron}")
    public void generateAnsSaveBatches() {
        hashGenerator.generateAndSaveBatches();
    }
}
