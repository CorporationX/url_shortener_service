package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${hash.clean.days-to-delete:365}")
    private int daysToDelete;

    @Scheduled(cron = "${hash.clean.cron}")
    @Transactional
    public void clean() {
        List<String> freeHashes = urlRepository.deleteOldUrls(daysToDelete);
        List<Hash> hashes = freeHashes.stream().map(hash -> Hash.builder().hash(hash).build()).toList();
        hashRepository.saveAll(hashes);
    }
}
