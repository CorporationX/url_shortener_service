package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashMapper hashMapper;

    @Scheduled(cron = "${clean-url-cron}")
    @Transactional
    void cleanUrl() {
        log.info("Start job: clean outdated url and populate hash");
        long startTime = System.currentTimeMillis();
        List<String> hashes = urlRepository.removeUrlOlderThanOneYear();
        hashRepository.saveAll(hashMapper.toEntities(hashes));
        long endTime = System.currentTimeMillis();
        log.info("Duration of execution: " + (endTime - startTime) / 1000 + " seconds.");
    }
}
