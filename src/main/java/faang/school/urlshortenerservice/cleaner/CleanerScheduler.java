package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${hash.scheduled}")
    @Transactional
    public void deleteOldUrls() {
        log.info("Started cleaning old urls");
        List<String> oldUrls = urlRepository.deleteOldUrls();

        hashRepository.saveAll(oldUrls.stream().map(Hash::new).toList());
    }
}
