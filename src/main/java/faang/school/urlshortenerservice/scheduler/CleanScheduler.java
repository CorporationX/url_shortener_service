package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Value("${spring.clean-scheduler.expiration-months}")
    private int urlExpirationMonths;

    @Scheduled(cron = "${spring.clean-scheduler.cron}")
    @Transactional
    public void clean() {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(urlExpirationMonths);

        List<Url> expiredUrls = urlRepository.findAllExpiredUrls(threshold);
        List<Hash> newHashes =
                expiredUrls.stream()
                        .map(oldUrl -> Hash.builder().hash(oldUrl.getHash()).build())
                        .toList();

        urlRepository.deleteAll(expiredUrls);
        hashRepository.saveAll(newHashes);

        log.info("New hashes have been saved: {}", newHashes);
    }
}
