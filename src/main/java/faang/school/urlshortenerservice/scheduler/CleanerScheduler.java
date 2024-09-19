package faang.school.urlshortenerservice.scheduler;


import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
private final HashRepository hashRepository;
private final UrlRepository urlRepository;

    @Scheduled(cron = "${hash.cache.cron}")
    @Transactional
    public List<Hash> clean(){
        List<Url> urls = urlRepository.clearOlderThanYear();

        List<Hash> hashes = urls.stream()
                .map(hash -> new Hash(hash.getHash()))
                .toList();

        log.info("Hashes to be saved: " + hashes.size());
        hashRepository.saveAll(hashes);
        log.info("Save operation completed");
        return hashes;
    }
}
